package ucne.edu.rocash.presentation.estacion.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.agenteVentas.usecase.ObserveAgentesUseCase
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.usecase.DeleteEstacionUseCase
import ucne.edu.rocash.domain.estacion.usecase.GetEstacionUseCase
import ucne.edu.rocash.domain.estacion.usecase.UpsertEstacionUseCase
import ucne.edu.rocash.domain.estacion.usecase.validateAgenteAsignado
import ucne.edu.rocash.domain.estacion.usecase.validateEstacionDireccion
import ucne.edu.rocash.domain.estacion.usecase.validateEstacionNombre
import javax.inject.Inject

@HiltViewModel
class EstacionFormViewModel @Inject constructor(
    private val upsertEstacionUseCase: UpsertEstacionUseCase,
    private val getEstacionUseCase: GetEstacionUseCase,
    private val deleteEstacionUseCase: DeleteEstacionUseCase,
    private val observeAgentesUseCase: ObserveAgentesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val navEstacionId: Int = savedStateHandle.get<Int>("estacionId") ?: 0

    private val _state = MutableStateFlow(EstacionFormUiState())
    val state: StateFlow<EstacionFormUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeAgentesUseCase().collectLatest { listaAgentes ->
                val activos = listaAgentes.filter { it.estado }
                _state.update { it.copy(agentesDisponibles = activos) }

                val currentAgenteId = _state.value.agenteId
                if (currentAgenteId != null && currentAgenteId != 0) {
                    val nombre = activos.find { it.agenteId == currentAgenteId }?.nombre ?: ""
                    _state.update { it.copy(agenteNombreSeleccionado = nombre) }
                }
            }
        }
        loadEstacion(navEstacionId)
    }

    fun onEvent(event: EstacionFormUiEvent) {
        when (event) {
            is EstacionFormUiEvent.Load -> loadEstacion(event.id)
            is EstacionFormUiEvent.NombreChanged -> _state.update { it.copy(nombre = event.value, nombreError = null) }
            is EstacionFormUiEvent.DireccionChanged -> _state.update { it.copy(direccion = event.value, direccionError = null) }
            is EstacionFormUiEvent.AgenteSeleccionado -> _state.update {
                it.copy(agenteId = event.id, agenteNombreSeleccionado = event.nombre, agenteError = null)
            }
            EstacionFormUiEvent.Save -> onSave()
            EstacionFormUiEvent.Delete -> onDelete()
            EstacionFormUiEvent.ErrorMostrado -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadEstacion(id: Int?) {
        if (id == null || id == 0) {
            _state.update { it.copy(isNew = true, estacionId = null) }
            return
        }

        viewModelScope.launch {
            val estacion = getEstacionUseCase(id)
            if (estacion != null) {
                _state.update {
                    it.copy(
                        isNew = false,
                        estacionId = estacion.estacionId,
                        nombre = estacion.nombre,
                        direccion = estacion.direccion,
                        agenteId = estacion.agenteId
                    )
                }
            }
        }
    }

    private fun onSave() {
        val currentState = state.value

        val nombreValidation = validateEstacionNombre(currentState.nombre)
        val direccionValidation = validateEstacionDireccion(currentState.direccion)
        val agenteValidation = validateAgenteAsignado(currentState.agenteId)

        if (!nombreValidation.isValid || !direccionValidation.isValid || !agenteValidation.isValid) {
            _state.update {
                it.copy(
                    nombreError = nombreValidation.error,
                    direccionError = direccionValidation.error,
                    agenteError = agenteValidation.error
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val estacion = EstacionVentas(
                estacionId = currentState.estacionId ?: 0,
                nombre = currentState.nombre,
                direccion = currentState.direccion,
                agenteId = currentState.agenteId!!
            )

            val result = upsertEstacionUseCase(estacion)
            result.onSuccess { newId ->
                _state.update { it.copy(isSaving = false, saved = true, estacionId = newId, isNew = false) }
            }.onFailure { error ->
                // Antes el fallo se descartaba en silencio: la pantalla se
                // quedaba quieta sin decir nada y parecía que no había pasado
                // nada. Una violación de clave foránea o una validación del
                // caso de uso terminaban invisibles.
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "No se pudo guardar la estación"
                    )
                }
            }
        }
    }

    private fun onDelete() {
        val id = state.value.estacionId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            deleteEstacionUseCase(id)
            _state.update { it.copy(isDeleting = false, deleted = true) }
        }
    }
}