package ucne.edu.rocash.presentation.estacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.model.EstacionVentas
import ucne.edu.rocash.domain.usecase.CrearEstacionUseCase
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class CrearEstacionViewModel @Inject constructor(
    private val crearEstacionUseCase: CrearEstacionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CrearEstacionUIState())
    val state: StateFlow<CrearEstacionUIState> = _state.asStateFlow()

    fun processIntent(intent: CrearEstacionUIEvent) {
        when (intent) {
            is CrearEstacionUIEvent.OnNombreChange -> _state.update { it.copy(nombre = intent.value) }
            is CrearEstacionUIEvent.OnDireccionChange -> _state.update { it.copy(direccion = intent.value) }
            is CrearEstacionUIEvent.OnAgenteIdChange -> _state.update { it.copy(agenteId = intent.value) }
            is CrearEstacionUIEvent.GuardarEstacion -> guardar()
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