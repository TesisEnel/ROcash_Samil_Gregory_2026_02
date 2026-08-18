package ucne.edu.rocash.presentation.agenteVentas.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ucne.edu.rocash.domain.agenteVentas.usecase.SearchAgentesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.usecase.DeleteAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.ObserveAgentesUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.UpsertAgenteUseCase

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AgenteListViewModel @Inject constructor(
    private val observeAgentesUseCase: ObserveAgentesUseCase,
    private val searchAgentesUseCase: SearchAgentesUseCase,
    private val deleteAgenteUseCase: DeleteAgenteUseCase,
    private val upsertAgenteUseCase: UpsertAgenteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AgenteListUiState(isLoading = true))
    val state: StateFlow<AgenteListUiState> = _state.asStateFlow()

    init {
        loadAgentes()
    }

    fun onEvent(event: AgenteListUiEvent) {
        when (event) {
            AgenteListUiEvent.Load -> loadAgentes()
            AgenteListUiEvent.Refresh -> loadAgentes()
            is AgenteListUiEvent.Delete -> onDelete(event.id)
            is AgenteListUiEvent.ShowMessage -> _state.update { it.copy(message = event.message) }
            AgenteListUiEvent.ClearMessage -> _state.update { it.copy(message = null) }
            AgenteListUiEvent.CreateNew -> _state.update { it.copy(navigateToCreate = true) }
            is AgenteListUiEvent.Edit -> _state.update {
                it.copy(navigateToEditId = event.id, agenteSeleccionado = null)
            }

            is AgenteListUiEvent.AgenteTocado ->
                _state.update { it.copy(agenteSeleccionado = event.agente) }

            AgenteListUiEvent.CerrarAcciones ->
                _state.update { it.copy(agenteSeleccionado = null) }

            is AgenteListUiEvent.GestionarDeuda -> _state.update {
                it.copy(navigateToDeudaId = event.id, agenteSeleccionado = null)
            }

            AgenteListUiEvent.NavegacionConsumida -> _state.update {
                it.copy(
                    navigateToCreate = false,
                    navigateToEditId = null,
                    navigateToDeudaId = null
                )
            }

            is AgenteListUiEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            is AgenteListUiEvent.ToggleEstado -> onToggleEstado(event.agente)
        }
    }

    fun loadAgentes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            _state.map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) observeAgentesUseCase() else searchAgentesUseCase(query)
                }
                .collectLatest { lista ->
                    _state.update { it.copy(isLoading = false, agentes = lista, message = null) }
                }
        }
    }

    private fun onDelete(id: Int) {
        viewModelScope.launch {
            deleteAgenteUseCase(id)
            onEvent(AgenteListUiEvent.ShowMessage("Agente eliminado"))
        }
    }

    private fun onToggleEstado(agente: AgenteVentas) {
        viewModelScope.launch {
            val actualizado = agente.copy(estado = !agente.estado)
            upsertAgenteUseCase(actualizado)
        }
    }
}