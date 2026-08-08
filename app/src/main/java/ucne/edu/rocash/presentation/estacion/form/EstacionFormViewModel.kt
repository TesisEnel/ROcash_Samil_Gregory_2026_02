package ucne.edu.rocash.presentation.estacion.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.agenteVentas.usecase.GetAgentesUseCase
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.usecase.GetEstacionPorIdUseCase
import ucne.edu.rocash.domain.estacion.usecase.SaveEstacionUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EstacionFormViewModel @Inject constructor(
    private val saveEstacionUseCase: SaveEstacionUseCase,
    private val getEstacionPorIdUseCase: GetEstacionPorIdUseCase,
    private val getAgentesUseCase: GetAgentesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EstacionFormUiState())
    val state: StateFlow<EstacionFormUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getAgentesUseCase().collectLatest { listaAgentes ->
                _state.update { it.copy(agentesDisponibles = listaAgentes.filter { agente -> agente.estado }) }

                val currentAgenteId = _state.value.agenteId
                if (currentAgenteId.isNotBlank()) {
                    val nombre = listaAgentes.find { it.id == currentAgenteId }?.nombre ?: ""
                    _state.update { it.copy(agenteNombreSeleccionado = nombre) }
                }
            }
        }
    }

    fun inicializar(estacionId: String?) {
        if (estacionId != null && _state.value.id == null) {
            viewModelScope.launch {
                val estacion = getEstacionPorIdUseCase(estacionId)
                if (estacion != null) {
                    _state.update {
                        it.copy(
                            id = estacion.id,
                            nombre = estacion.nombre,
                            direccion = estacion.direccion,
                            agenteId = estacion.agenteId
                        )
                    }
                }
            }
        }
    }

    fun processIntent(intent: EstacionFormUiEvent) {
        when (intent) {
            is EstacionFormUiEvent.OnNombreChange -> _state.update { it.copy(nombre = intent.value) }
            is EstacionFormUiEvent.OnDireccionChange -> _state.update { it.copy(direccion = intent.value) }
            is EstacionFormUiEvent.OnAgenteSeleccionado -> _state.update {
                it.copy(agenteId = intent.id, agenteNombreSeleccionado = intent.nombre)
            }
            is EstacionFormUiEvent.ResetSuccessState -> _state.update { it.copy(isSuccess = false) }
            is EstacionFormUiEvent.GuardarEstacion -> guardar()
        }
    }

    private fun guardar() {
        val currentState = _state.value
        if (currentState.nombre.isBlank() || currentState.direccion.isBlank() || currentState.agenteId.isBlank()) {
            _state.update { it.copy(errorMessage = "Todos los campos son obligatorios") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val estacion = EstacionVentas(
                    id = currentState.id ?: UUID.randomUUID().toString(),
                    nombre = currentState.nombre,
                    direccion = currentState.direccion,
                    agenteId = currentState.agenteId
                )

                saveEstacionUseCase(estacion)

                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}