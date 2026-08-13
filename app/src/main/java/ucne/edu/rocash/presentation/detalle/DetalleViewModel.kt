package ucne.edu.rocash.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ProcesarRecoleccionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.validateMontoNumerico
import javax.inject.Inject

@HiltViewModel
class DetalleViewModel @Inject constructor(
    private val procesarRecoleccionUseCase: ProcesarRecoleccionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetalleUiState())
    val state: StateFlow<DetalleUiState> = _state.asStateFlow()

    fun onEvent(event: DetalleUiEvent) {
        when (event) {
            is DetalleUiEvent.Load -> {
                _state.update {
                    it.copy(
                        hojaRutaId = event.hojaRutaId,
                        estacionId = event.estacionId,
                        agenteId = event.agenteId,
                        nombreEstacion = event.nombre
                    )
                }
            }
            is DetalleUiEvent.VentaBrutaChanged -> actualizarValores(ventaBruta = event.value)
            is DetalleUiEvent.ComisionChanged -> actualizarValores(comision = event.value)
            is DetalleUiEvent.MontoRecolectadoChanged -> actualizarValores(recolectado = event.value)
            DetalleUiEvent.Save -> onSave()
        }
    }

    private fun actualizarValores(
        ventaBruta: String? = null,
        comision: String? = null,
        recolectado: String? = null
    ) {
        _state.update { currentState ->
            val newVentaBruta = ventaBruta ?: currentState.ventaBruta
            val newComision = comision ?: currentState.comisionCliente
            val newRecolectado = recolectado ?: currentState.montoRecolectado

            val copyState = currentState.copy(
                ventaBruta = newVentaBruta,
                comisionCliente = newComision,
                montoRecolectado = newRecolectado,
                ventaBrutaError = if (ventaBruta != null) null else currentState.ventaBrutaError,
                comisionError = if (comision != null) null else currentState.comisionError,
                montoRecolectadoError = if (recolectado != null) null else currentState.montoRecolectadoError,
                errorMessage = null
            )

            val vb = newVentaBruta.toDoubleOrNull() ?: 0.0
            val cc = newComision.toDoubleOrNull() ?: 0.0
            val rec = newRecolectado.toDoubleOrNull() ?: 0.0

            val esperado = vb - cc
            val deuda = if (rec < esperado) esperado - rec else 0.0

            copyState.copy(
                montoEsperado = esperado,
                deudaGenerada = deuda
            )
        }
    }

    private fun onSave() {
        val currentState = state.value

        val vbResult = validateMontoNumerico(currentState.ventaBruta, "Venta Bruta")
        val ccResult = validateMontoNumerico(currentState.comisionCliente, "Comisión Cliente")
        val mrResult = validateMontoNumerico(currentState.montoRecolectado, "Monto Recolectado")

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
            val result = procesarRecoleccionUseCase(
                hojaRutaId = currentState.hojaRutaId,
                estacionId = currentState.estacionId,
                agenteId1 = currentState.agenteId,
                agenteId2 = null,
                ventaBrutaStr = currentState.ventaBruta,
                comisionClienteStr = currentState.comisionCliente,
                montoRecolectadoStr = currentState.montoRecolectado
            )

            result.onSuccess {
                _state.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { exception ->
                _state.update { it.copy(isSaving = false, errorMessage = exception.localizedMessage) }
            }
        }
    }
}