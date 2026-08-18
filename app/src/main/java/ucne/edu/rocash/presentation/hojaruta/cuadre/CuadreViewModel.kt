package ucne.edu.rocash.presentation.hojaRuta.cuadre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.estacion.usecase.GetEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.CalcularCuadreUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ObtenerCuadreDeEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ProcesarRecoleccionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.validateMontoNumerico
import javax.inject.Inject

/** Cuál de los tres campos de dinero acaba de tocar el usuario. */
private enum class CampoMonto { VENTA_BRUTA, COMISION, MONTO_RECOLECTADO }

/**
 * Antes `actualizarValores()` contenía esto:
 *
 *     val esperado = vb - cc
 *     deudaGenerada = (esperado - rec).coerceAtLeast(0.0)
 *
 * Es decir, la fórmula del cuadre escrita a mano en la capa de presentación,
 * mientras `CalculoCuadre` en el dominio ya la tenía —y ProcesarRecoleccionUseCase
 * la usaba al guardar—. La pantalla y la base de datos podían mostrar números
 * distintos con sólo tocar una de las dos copias. Ahora ambas pasan por
 * [CalcularCuadreUseCase] y el ViewModel no hace aritmética de dinero.
 */
@HiltViewModel
class CuadreViewModel @Inject constructor(
    private val procesarRecoleccionUseCase: ProcesarRecoleccionUseCase,
    private val obtenerCuadreDeEstacionUseCase: ObtenerCuadreDeEstacionUseCase,
    private val calcularCuadreUseCase: CalcularCuadreUseCase,
    private val getEstacionUseCase: GetEstacionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CuadreUiState())
    val state: StateFlow<CuadreUiState> = _state.asStateFlow()

    fun onEvent(event: CuadreUiEvent) {
        when (event) {
            is CuadreUiEvent.Load -> cargar(event)

            is CuadreUiEvent.VentaBrutaChanged ->
                editarMonto(CampoMonto.VENTA_BRUTA, event.value)

            is CuadreUiEvent.ComisionChanged ->
                editarMonto(CampoMonto.COMISION, event.value)

            is CuadreUiEvent.MontoRecolectadoChanged ->
                editarMonto(CampoMonto.MONTO_RECOLECTADO, event.value)

            is CuadreUiEvent.NotaChanged ->
                _state.update { it.copy(notaIncidencia = event.value) }

            CuadreUiEvent.PedirConfirmacion -> pedirConfirmacion()

            CuadreUiEvent.CancelarConfirmacion ->
                _state.update { it.copy(mostrarDialogoConfirmacion = false) }

            CuadreUiEvent.Save -> guardar()

            CuadreUiEvent.ErrorMostrado ->
                _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun cargar(event: CuadreUiEvent.Load) {
        _state.update {
            it.copy(
                hojaRutaId = event.hojaRutaId,
                estacionId = event.estacionId,
                agenteId = event.agenteId,
                nombreEstacion = event.nombre,
                isLoading = true
            ).conDerivadosResueltos()
        }

        viewModelScope.launch {
            val estacion = getEstacionUseCase(event.estacionId)
            val previo = obtenerCuadreDeEstacionUseCase(event.hojaRutaId, event.estacionId)

            _state.update { actual ->
                if (previo == null) {
                    actual.copy(
                        isLoading = false,
                        isNew = true,
                        agenteId2 = estacion?.agenteId2
                    ).conDerivadosResueltos()
                } else {
                    actual.copy(
                        isLoading = false,
                        isNew = false,
                        agenteId2 = estacion?.agenteId2,
                        ventaBruta = previo.ventaBruta.toString(),
                        comisionCliente = previo.comisionCliente.toString(),
                        montoRecolectado = previo.montoRecolectado.toString(),
                        notaIncidencia = previo.notaIncidencia.orEmpty(),
                        montoEsperado = previo.montoEsperado,
                        deudaGenerada = previo.montoDeuda
                    ).conDerivadosResueltos()
                }
            }
        }
    }

    /**
     * Resuelve los tres textos, le pide el cálculo al dominio y deposita el
     * resultado. Sólo se limpia el error del campo que el usuario acaba de
     * editar; los otros dos se conservan.
     */
    private fun editarMonto(campo: CampoMonto, valor: String) {
        val actual = _state.value

        val ventaBruta =
            if (campo == CampoMonto.VENTA_BRUTA) valor else actual.ventaBruta
        val comisionCliente =
            if (campo == CampoMonto.COMISION) valor else actual.comisionCliente
        val montoRecolectado =
            if (campo == CampoMonto.MONTO_RECOLECTADO) valor else actual.montoRecolectado

        val calculo = calcularCuadreUseCase.desdeTexto(
            ventaBruta = ventaBruta,
            comisionCliente = comisionCliente,
            montoRecolectado = montoRecolectado
        )

        _state.update {
            it.copy(
                ventaBruta = ventaBruta,
                comisionCliente = comisionCliente,
                montoRecolectado = montoRecolectado,
                ventaBrutaError =
                    if (campo == CampoMonto.VENTA_BRUTA) null else it.ventaBrutaError,
                comisionError =
                    if (campo == CampoMonto.COMISION) null else it.comisionError,
                montoRecolectadoError =
                    if (campo == CampoMonto.MONTO_RECOLECTADO) null
                    else it.montoRecolectadoError,
                montoEsperado = calculo.montoEsperado,
                deudaGenerada = calculo.montoDeuda,
                errorMessage = null
            ).conDerivadosResueltos()
        }
    }

    /**
     * Valida antes de abrir el diálogo. Si los montos están mal, se muestran los
     * errores en los campos y no se abre nada: no tiene sentido pedirle al
     * usuario que confirme cifras que ni siquiera son números.
     */
    private fun pedirConfirmacion() {
        val actual = _state.value
        if (actual.isSaving) return

        val vbResult = validateMontoNumerico(actual.ventaBruta, "Venta Bruta")
        val ccResult = validateMontoNumerico(actual.comisionCliente, "Comisión Cliente")
        val mrResult = validateMontoNumerico(actual.montoRecolectado, "Monto Recolectado")

        if (!vbResult.isValid || !ccResult.isValid || !mrResult.isValid) {
            _state.update {
                it.copy(
                    ventaBrutaError = vbResult.error,
                    comisionError = ccResult.error,
                    montoRecolectadoError = mrResult.error,
                    mostrarDialogoConfirmacion = false
                ).conDerivadosResueltos()
            }
            return
        }

        _state.update { it.copy(mostrarDialogoConfirmacion = true) }
    }

    private fun guardar() {
        val actual = _state.value
        if (actual.isSaving) return

        _state.update {
            it.copy(
                isSaving = true,
                errorMessage = null,
                mostrarDialogoConfirmacion = false
            ).conDerivadosResueltos()
        }

        viewModelScope.launch {
            procesarRecoleccionUseCase(
                hojaRutaId = actual.hojaRutaId,
                estacionId = actual.estacionId,
                agenteId1 = actual.agenteId,
                agenteId2 = actual.agenteId2,
                ventaBrutaStr = actual.ventaBruta,
                comisionClienteStr = actual.comisionCliente,
                montoRecolectadoStr = actual.montoRecolectado,
                notaIncidencia = actual.notaIncidencia.takeIf { it.isNotBlank() }
            )
                .onSuccess {
                    _state.update {
                        it.copy(isSaving = false, saved = true).conDerivadosResueltos()
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "No se pudo guardar el cuadre"
                        ).conDerivadosResueltos()
                    }
                }
        }
    }

    /**
     * Único lugar donde se decide qué se habilita y qué se pinta.
     *
     * `puedeGuardar` es exactamente la regla que antes vivía dentro del UiState;
     * la diferencia es que ahora se evalúa una vez por transición y no una vez
     * por recomposición.
     */
    private fun CuadreUiState.conDerivadosResueltos(): CuadreUiState = copy(
        hayDeuda = deudaGenerada > 0.0,
        deudaSeReparte = deudaGenerada > 0.0 && agenteId2 != null,
        puedeGuardar = !isSaving &&
                !isLoading &&
                ventaBruta.isNotBlank() &&
                comisionCliente.isNotBlank() &&
                montoRecolectado.isNotBlank()
    )
}
