package ucne.edu.rocash.presentation.hojaRuta.cuadre

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.estacion.usecase.GetEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.CalcularCuadreUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ObtenerCuadreDeEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ProcesarRecoleccionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.validateMontoNumerico
import ucne.edu.rocash.presentation.core.MviViewModel
import javax.inject.Inject

/**
 * Orquesta el formulario de cuadre: lee intenciones, pide cálculos al dominio,
 * reduce estado y emite efectos. No hace aritmética de dinero.
 *
 * Antes `actualizarValores()` contenía esto:
 *
 *     val esperado = vb - cc
 *     deudaGenerada = (esperado - rec).coerceAtLeast(0.0)
 *
 * Es decir, la fórmula del cuadre escrita a mano en la capa de presentación,
 * mientras `CalculoCuadre` en el dominio ya la tenía —y ProcesarRecoleccionUseCase
 * la usaba al guardar—. La pantalla y la base de datos podían mostrar números
 * distintos con sólo tocar una de las dos copias. Ahora ambas pasan por
 * [CalcularCuadreUseCase].
 */
@HiltViewModel
class CuadreViewModel @Inject constructor(
    private val procesarRecoleccionUseCase: ProcesarRecoleccionUseCase,
    private val obtenerCuadreDeEstacionUseCase: ObtenerCuadreDeEstacionUseCase,
    private val calcularCuadreUseCase: CalcularCuadreUseCase,
    private val getEstacionUseCase: GetEstacionUseCase
) : MviViewModel<CuadreUiState, CuadreUiEvent>(CuadreUiState()) {

    override fun onEvent(event: CuadreUiEvent) {
        when (event) {
            is CuadreUiEvent.Load -> cargar(event)

            is CuadreUiEvent.VentaBrutaChanged ->
                editarMonto(CampoMonto.VENTA_BRUTA, event.value)

            is CuadreUiEvent.ComisionChanged ->
                editarMonto(CampoMonto.COMISION, event.value)

            is CuadreUiEvent.MontoRecolectadoChanged ->
                editarMonto(CampoMonto.MONTO_RECOLECTADO, event.value)

            is CuadreUiEvent.NotaChanged ->
                reduce { CuadreReducer.conNota(it, event.value) }

            CuadreUiEvent.Save -> guardar()

            CuadreUiEvent.ErrorMostrado -> reduce(CuadreReducer::sinMensaje)
        }
    }

    private fun cargar(event: CuadreUiEvent.Load) {
        reduce { estado ->
            CuadreReducer.cargando(
                estado = estado,
                hojaRutaId = event.hojaRutaId,
                estacionId = event.estacionId,
                agenteId = event.agenteId,
                nombreEstacion = event.nombre
            )
        }

        viewModelScope.launch {
            val estacion = getEstacionUseCase(event.estacionId)
            val previo = obtenerCuadreDeEstacionUseCase(event.hojaRutaId, event.estacionId)

            reduce { estado ->
                if (previo == null) {
                    CuadreReducer.comoCuadreNuevo(estado, estacion?.agenteId2)
                } else {
                    CuadreReducer.conCuadrePrevio(estado, previo, estacion?.agenteId2)
                }
            }
        }
    }

    /**
     * Resuelve los tres textos, pide el cálculo al dominio y deposita el
     * resultado. La única responsabilidad del ViewModel aquí es de coordinación.
     */
    private fun editarMonto(campo: CampoMonto, valor: String) {
        val actual = estadoActual

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

        reduce { estado ->
            CuadreReducer.conMontos(
                estado = estado,
                campoEditado = campo,
                ventaBruta = ventaBruta,
                comisionCliente = comisionCliente,
                montoRecolectado = montoRecolectado,
                calculo = calculo
            )
        }
    }

    private fun guardar() {
        val actual = estadoActual
        if (actual.isSaving) return

        val vbResult = validateMontoNumerico(actual.ventaBruta, "Venta Bruta")
        val ccResult = validateMontoNumerico(actual.comisionCliente, "Comisión Cliente")
        val mrResult = validateMontoNumerico(actual.montoRecolectado, "Monto Recolectado")

        if (!vbResult.isValid || !ccResult.isValid || !mrResult.isValid) {
            reduce { estado ->
                CuadreReducer.conErroresDeValidacion(
                    estado = estado,
                    ventaBrutaError = vbResult.error,
                    comisionError = ccResult.error,
                    montoRecolectadoError = mrResult.error
                )
            }
            return
        }

        reduce(CuadreReducer::guardando)

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
                    reduce(CuadreReducer::guardadoExitoso)
                }
                .onFailure { error ->
                    reduce { estado ->
                        CuadreReducer.guardadoFallido(
                            estado = estado,
                            mensaje = error.message ?: "No se pudo guardar el cuadre"
                        )
                    }
                }
        }
    }
}
