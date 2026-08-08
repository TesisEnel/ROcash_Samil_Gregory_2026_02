package ucne.edu.rocash.presentation.agenteVentas.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.usecase.SaveAgenteUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgenteFormViewModel @Inject constructor(
    private val saveAgenteUseCase: SaveAgenteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AgenteFormUiState())
    val state: StateFlow<AgenteFormUiState> = _state.asStateFlow()

    fun processIntent(intent: AgenteFormUiEvent) {
        when (intent) {
            is AgenteFormUiEvent.OnNombreChange -> _state.update { it.copy(nombre = intent.nombre) }
            is AgenteFormUiEvent.OnTelefonoChange -> _state.update { it.copy(telefono = intent.telefono) }
            is AgenteFormUiEvent.ResetSuccessState -> _state.update { it.copy(isSuccess = false) }
            is AgenteFormUiEvent.GuardarAgente -> guardar()
        }
    }

    private fun guardar() {
        val current = _state.value
        if (current.nombre.isBlank() || current.telefono.isBlank()) {
            _state.update { it.copy(errorMessage = "Llene todos los campos") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val nuevo = AgenteVentas(
                    id = UUID.randomUUID().toString(),
                    nombre = current.nombre,
                    telefono = current.telefono,
                    deudaAcumulada = 0.0,
                    estado = true
                )
                saveAgenteUseCase(nuevo)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}