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
import ucne.edu.rocash.domain.usecase.CrearEstacionUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EstacionFormViewModel @Inject constructor(
    private val crearEstacionUseCase: CrearEstacionUseCase,
    private val getAgentesUseCase: GetAgentesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EstacionFormUiState())
    val state: StateFlow<EstacionFormUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getAgentesUseCase().collectLatest { listaAgentes ->
                _state.update { it.copy(agentesDisponibles = listaAgentes.filter { agente -> agente.estado }) }
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
                val nuevaEstacion = EstacionVentas(
                    id = UUID.randomUUID().toString(),
                    nombre = currentState.nombre,
                    direccion = currentState.direccion,
                    agenteId = currentState.agenteId
                )

                crearEstacionUseCase(nuevaEstacion)

                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}