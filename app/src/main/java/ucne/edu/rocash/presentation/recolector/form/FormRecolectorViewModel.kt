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
import ucne.edu.rocash.domain.recolector.repository.RecolectorRepository
import ucne.edu.rocash.domain.recolector.usecase.SaveRecolectorUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FormRecolectorViewModel @Inject constructor(
    private val saveRecolectorUseCase: SaveRecolectorUseCase,
    private val repository: RecolectorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FormRecolectorUiState())
    val state: StateFlow<FormRecolectorUiState> = _state.asStateFlow()

    fun processIntent(intent: FormRecolectorUiEvent) {
        when (intent) {
            is FormRecolectorUiEvent.Inicializar -> {
                cargarRecolector(intent.id)
            }
            is FormRecolectorUiEvent.OnNombreChange -> {
                _state.update { it.copy(nombre = intent.nombre, nombreError = null) }
            }
            is FormRecolectorUiEvent.OnTelefonoChange -> {
                _state.update { it.copy(telefono = intent.telefono, telefonoError = null) }
            }
            is FormRecolectorUiEvent.OnCedulaChange ->{
                _state.update { it.copy(cedula = intent.cedula, cedulaError = null) }
            }
            is FormRecolectorUiEvent.GuardarRecolector -> {
                validarYGuardar()
            }
            is FormRecolectorUiEvent.ResetSuccessState -> {
                _state.update { it.copy(isSuccess = false) }
            }
        }
    }

    private fun cargarRecolector(id: String?) {
        if (id == null) return

        _state.update { it.copy(isLoading = true, recolectorId = id) }

        viewModelScope.launch {
            try {
                val recolector = repository.obtenerRecolectorPorId(id)
                if (recolector != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            nombre = recolector.nombre,
                            telefono = recolector.telefono,
                            cedula = recolector.cedula
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Recolector no encontrado") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    private fun validarYGuardar() {
        val currentState = _state.value

        val nombreError = if (currentState.nombre.isBlank()) "El nombre es obligatorio" else null
        val telefonoError = if (currentState.telefono.length < 10) "Debe tener al menos 10 dígitos" else null
        val cedulaError = if (currentState.cedula.length != 11) "La cédula debe tener exactamente 11 dígitos" else null

        if (nombreError != null || telefonoError != null || cedulaError != null) {
            _state.update {
                it.copy(
                    nombreError = nombreError,
                    telefonoError = telefonoError,
                    cedulaError = cedulaError
                )
            }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val idGuardar = currentState.recolectorId ?: UUID.randomUUID().toString()

                val recolector = Recolector(
                    id = idGuardar,
                    nombre = currentState.nombre,
                    telefono = currentState.telefono,
                    cedula = currentState.cedula,
                    estado = true
                )

                saveRecolectorUseCase(recolector)

                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}