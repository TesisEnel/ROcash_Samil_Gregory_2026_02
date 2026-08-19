package ucne.edu.rocash.presentation.estacion.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.abonoDeuda.usecase.ObservarAbonosDeAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.ObservarAgenteUseCase
import ucne.edu.rocash.domain.estacion.usecase.GetEstacionUseCase
import javax.inject.Inject

@HiltViewModel
class EstacionDetalleViewModel @Inject constructor(
    private val getEstacionUseCase: GetEstacionUseCase,
    private val observarAgenteUseCase: ObservarAgenteUseCase,
    private val observarAbonosUseCase: ObservarAbonosDeAgenteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EstacionDetalleUiState())
    val state: StateFlow<EstacionDetalleUiState> = _state.asStateFlow()

    private var observacion: Job? = null

    fun onEvent(event: EstacionDetalleUiEvent) {
        when (event) {
            is EstacionDetalleUiEvent.Load -> cargarDetalles(event.estacionId)
            EstacionDetalleUiEvent.ErrorMostrado -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun cargarDetalles(estacionId: Int) {
        if (observacion != null) return
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val estacion = getEstacionUseCase(estacionId)

            if (estacion == null) {
                _state.update { it.copy(isLoading = false, errorMessage = "Estación no encontrada") }
                return@launch
            }

            observacion = launch {
                combine(
                    observarAgenteUseCase(estacion.agenteId),
                    observarAbonosUseCase(estacion.agenteId)
                ) { agente, abonos ->
                    _state.update { actual ->
                        actual.copy(
                            isLoading = false,
                            estacion = estacion,
                            agente = agente,
                            historialAbonos = abonos
                        )
                    }
                }.collect {}
            }
        }
    }
}