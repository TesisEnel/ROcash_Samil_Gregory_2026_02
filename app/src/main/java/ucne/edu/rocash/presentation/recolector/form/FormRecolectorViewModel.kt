package ucne.edu.rocash.presentation.recolector.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.recolector.model.Recolector
import ucne.edu.rocash.domain.recolector.usecase.SaveRecolectorUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FormRecolectorViewModel @Inject constructor(
    private val saveRecolectorUseCase: SaveRecolectorUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FormRecolectorUiState())
    val state: StateFlow<FormRecolectorUiState> = _state.asStateFlow()

    fun processIntent(intent: FormRecolectorUiEvent) {
        when (intent) {
            is FormRecolectorUiEvent.OnNombreChange -> {
                _state.update { it.copy(nombre = intent.nombre) }
            }
            is FormRecolectorUiEvent.OnTelefonoChange -> {
                _state.update { it.copy(telefono = intent.telefono) }
            }
            is FormRecolectorUiEvent.GuardarRecolector -> {
                guardar()
            }
            is FormRecolectorUiEvent.ResetSuccessState -> {
                _state.update { it.copy(isSuccess = false) }
            }
        }
    }

    private fun guardar() {
        val currentState = _state.value

        if (currentState.nombre.isBlank() || currentState.telefono.isBlank()) {
            _state.update { it.copy(errorMessage = "Todos los campos son obligatorios") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val nuevo = Recolector(
                    id = UUID.randomUUID().toString(),
                    nombre = currentState.nombre,
                    telefono = currentState.telefono,
                    estado = true
                )

                saveRecolectorUseCase(nuevo)

                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}