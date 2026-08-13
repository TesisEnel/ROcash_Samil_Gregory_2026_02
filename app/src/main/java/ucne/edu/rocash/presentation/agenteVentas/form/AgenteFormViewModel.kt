package ucne.edu.rocash.presentation.agenteVentas.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.usecase.DeleteAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.GetAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.UpsertAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.validateNombre
import ucne.edu.rocash.domain.agenteVentas.usecase.validateTelefono
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgenteFormViewModel @Inject constructor(
    private val getAgenteUseCase: GetAgenteUseCase,
    private val upsertAgenteUseCase: UpsertAgenteUseCase,
    private val deleteAgenteUseCase: DeleteAgenteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val agenteId: Int = savedStateHandle.get<Int>("agenteId") ?: 0

    private val _state = MutableStateFlow(AgenteFormUiState())
    val state: StateFlow<AgenteFormUiState> = _state.asStateFlow()

    init {
        loadAgente(agenteId)
    }

    fun onEvent(event: AgenteFormUiEvent) {
        when (event) {
            is AgenteFormUiEvent.Load -> loadAgente(event.id)
            is AgenteFormUiEvent.NombreChanged -> _state.update {
                it.copy(nombre = event.value, nombreError = null)
            }
            is AgenteFormUiEvent.TelefonoChanged -> _state.update {
                it.copy(telefono = event.value, telefonoError = null)
            }
            AgenteFormUiEvent.Save -> onSave()
            AgenteFormUiEvent.Delete -> onDelete()
        }
    }

    private fun loadAgente(id: Int?) {
        if (id == null || id == 0) {
            _state.update { it.copy(isNew = true, agenteId = null) }
            return
        }

        viewModelScope.launch {
            val agente = getAgenteUseCase(id)
            if (agente != null) {
                _state.update {
                    it.copy(
                        isNew = false,
                        agenteId = agente.agenteId,
                        nombre = agente.nombre,
                        telefono = agente.telefono
                    )
                }
            } else {
                _state.update { it.copy(isNew = true, agenteId = null) }
            }
        }
    }

    private fun onSave() {
        val nombre = state.value.nombre
        val nombreValidation = validateNombre(nombre)
        val telefonoValidation = validateTelefono(state.value.telefono)

        if (!nombreValidation.isValid || !telefonoValidation.isValid) {
            _state.update {
                it.copy(
                    nombreError = nombreValidation.error,
                    telefonoError = telefonoValidation.error
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val agente = AgenteVentas(
                agenteId = state.value.agenteId ?: 0,
                nombre = nombre,
                telefono = state.value.telefono,
                // Conservar defaults o consultar el objeto real si necesitas actualizar
                deudaAcumulada = 0.0,
                estado = true
            )

            val result = upsertAgenteUseCase(agente)
            result.onSuccess { newId ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        saved = true,
                        agenteId = newId,
                        isNew = false
                    )
                }
            }.onFailure {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun onDelete() {
        val id = state.value.agenteId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            deleteAgenteUseCase(id)
            _state.update { it.copy(isDeleting = false, deleted = true) }
        }
    }
}