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
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ObtenerCuadreDeEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ProcesarRecoleccionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.validateMontoNumerico
import javax.inject.Inject

@HiltViewModel
class CuadreViewModel @Inject constructor(
    private val procesarRecoleccionUseCase: ProcesarRecoleccionUseCase,
    private val obtenerCuadreDeEstacionUseCase: ObtenerCuadreDeEstacionUseCase,
    private val getEstacionUseCase: GetEstacionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CuadreUiState())
    val state: StateFlow<CuadreUiState> = _state.asStateFlow()

    fun onEvent(event: CuadreUiEvent) {
        when (event) {
            is CuadreUiEvent.Load -> cargar(event)
            is CuadreUiEvent.VentaBrutaChanged -> actualizarValores(ventaBruta = event.value)
            is CuadreUiEvent.ComisionChanged -> actualizarValores(comision = event.value)
            is CuadreUiEvent.MontoRecolectadoChanged ->
                actualizarValores(recolectado = event.value)
            is CuadreUiEvent.NotaChanged -> _state.update { it.copy(notaIncidencia = event.value) }
            CuadreUiEvent.Save -> onSave()
            CuadreUiEvent.ErrorMostrado -> _state.update { it.copy(errorMessage = null) }
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
            )
        }

        viewModelScope.launch {
            val estacion = getEstacionUseCase(event.estacionId)

            val previo = obtenerCuadreDeEstacionUseCase(event.hojaRutaId, event.estacionId)

            _state.update { current ->
                if (previo == null) {
                    current.copy(
                        isLoading = false,
                        isNew = true,
                        agenteId2 = estacion?.agenteId2
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        isNew = false,
                        agenteId2 = estacion?.agenteId2,
                        ventaBruta = previo.ventaBruta.toString(),
                        comisionCliente = previo.comisionCliente.toString(),
                        montoRecolectado = previo.montoRecolectado.toString(),
                        notaIncidencia = previo.notaIncidencia.orEmpty(),
                        montoEsperado = previo.montoEsperado,
                        deudaGenerada = previo.montoDeuda
                    )
                }
            }
        }
    }

    private fun actualizarValores(
        ventaBruta: String? = null,
        comision: String? = null,
        recolectado: String? = null
    ) {
        _state.update { current ->
            val nuevaVentaBruta = ventaBruta ?: current.ventaBruta
            val nuevaComision = comision ?: current.comisionCliente
            val nuevoRecolectado = recolectado ?: current.montoRecolectado

            val vb = nuevaVentaBruta.toDoubleOrNull() ?: 0.0
            val cc = nuevaComision.toDoubleOrNull() ?: 0.0
            val rec = nuevoRecolectado.toDoubleOrNull() ?: 0.0

            val esperado = vb - cc

            current.copy(
                ventaBruta = nuevaVentaBruta,
                comisionCliente = nuevaComision,
                montoRecolectado = nuevoRecolectado,
                ventaBrutaError = if (ventaBruta != null) null else current.ventaBrutaError,
                comisionError = if (comision != null) null else current.comisionError,
                montoRecolectadoError =
                    if (recolectado != null) null else current.montoRecolectadoError,
                errorMessage = null,
                montoEsperado = esperado,
                deudaGenerada = (esperado - rec).coerceAtLeast(0.0)
            )
        }
    }

    private fun onSave() {
        val current = _state.value
        if (current.isSaving) return

        val vbResult = validateMontoNumerico(current.ventaBruta, "Venta Bruta")
        val ccResult = validateMontoNumerico(current.comisionCliente, "Comisión Cliente")
        val mrResult = validateMontoNumerico(current.montoRecolectado, "Monto Recolectado")

        if (!vbResult.isValid || !ccResult.isValid || !mrResult.isValid) {
            _state.update {
                it.copy(
                    ventaBrutaError = vbResult.error,
                    comisionError = ccResult.error,
                    montoRecolectadoError = mrResult.error
                )
            }
            return
        }

        _state.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            procesarRecoleccionUseCase(
                hojaRutaId = current.hojaRutaId,
                estacionId = current.estacionId,
                agenteId1 = current.agenteId,
                agenteId2 = current.agenteId2,
                ventaBrutaStr = current.ventaBruta,
                comisionClienteStr = current.comisionCliente,
                montoRecolectadoStr = current.montoRecolectado,
                notaIncidencia = current.notaIncidencia.takeIf { it.isNotBlank() }
            )
                .onSuccess {
                    _state.update { it.copy(isSaving = false, saved = true) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "No se pudo guardar el cuadre"
                        )
                    }
                }
        }
    }
}