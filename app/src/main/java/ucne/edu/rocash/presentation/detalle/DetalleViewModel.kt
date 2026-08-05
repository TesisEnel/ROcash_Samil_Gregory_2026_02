package ucne.edu.rocash.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.usecase.ProcesarRecoleccionEstacionUseCase
import javax.inject.Inject

@HiltViewModel
class DetalleViewModel @Inject constructor(
    private val procesarRecoleccionUseCase: ProcesarRecoleccionEstacionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetalleUIState())
    val state: StateFlow<DetalleUIState> = _state.asStateFlow()

    fun processIntent(intent: DetalleUIEvent) {
        when (intent) {
            is DetalleUIEvent.Inicializar -> {
                _state.update {
                    it.copy(
                        estacionId = intent.estacionId,
                        agenteId = intent.agenteId,
                        nombreEstacion = intent.nombre
                    )
                }
            }
            is DetalleUIEvent.OnVentaBrutaChange -> actualizarValores(ventaBruta = intent.value)
            is DetalleUIEvent.OnPorcentajeChange -> actualizarValores(porcentaje = intent.value)
            is DetalleUIEvent.OnMontoRecolectadoChange -> actualizarValores(recolectado = intent.value)
            is DetalleUIEvent.ProcesarRecoleccion -> guardarRecoleccion()
        }
    }

    private fun actualizarValores(
        ventaBruta: String? = null,
        porcentaje: String? = null,
        recolectado: String? = null
    ) {
        _state.update { currentState ->
            val newVentaBruta = ventaBruta ?: currentState.ventaBruta
            val newPorcentaje = porcentaje ?: currentState.porcentajeCliente
            val newRecolectado = recolectado ?: currentState.montoRecolectado

            // Convertimos a Double seguro
            val vb = newVentaBruta.toDoubleOrNull() ?: 0.0
            val pc = newPorcentaje.toDoubleOrNull() ?: 0.0
            val rec = newRecolectado.toDoubleOrNull() ?: 0.0

            val esperado = vb - pc
            val deuda = if (rec < esperado) esperado - rec else 0.0

            currentState.copy(
                ventaBruta = newVentaBruta,
                porcentajeCliente = newPorcentaje,
                montoRecolectado = newRecolectado,
                montoEsperado = esperado,
                deudaGenerada = deuda
            )
        }
    }

    private fun guardarRecoleccion() {
        val currentState = _state.value
        if (currentState.ventaBruta.isBlank() || currentState.montoRecolectado.isBlank()) {
            _state.update { it.copy(errorMessage = "Llene los campos obligatorios") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                procesarRecoleccionUseCase(
                    hojaRutaId = "RUTA-ACTIVA-ID",
                    estacionId = currentState.estacionId,
                    agenteId = currentState.agenteId,
                    ventaBruta = currentState.ventaBruta.toDoubleOrNull() ?: 0.0,
                    porcentajeCliente = currentState.porcentajeCliente.toDoubleOrNull() ?: 0.0,
                    montoRecolectado = currentState.montoRecolectado.toDoubleOrNull() ?: 0.0
                )

                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}