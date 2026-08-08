package ucne.edu.rocash.presentation.agenteVentas.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ucne.edu.rocash.domain.agenteVentas.usecase.GetAgentesUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.SaveAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.SearchAgentesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AgenteListViewModel @Inject constructor(
    private val getAgentesUseCase: GetAgentesUseCase,
    private val searchAgentesUseCase: SearchAgentesUseCase,
    private val saveAgenteUseCase: SaveAgenteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AgenteListUiState())
    val state: StateFlow<AgenteListUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) getAgentesUseCase() else searchAgentesUseCase(query)
                }
                .collectLatest { lista ->
                    _state.update { it.copy(isLoading = false, agentes = lista, errorMessage = null) }
                }
        }
    }

    fun processIntent(intent: AgenteListUiEvent) {
        when (intent) {
            is AgenteListUiEvent.OnSearchQueryChange -> _state.update { it.copy(searchQuery = intent.query) }
            is AgenteListUiEvent.ToggleEstadoAgente -> {
                viewModelScope.launch {
                    try {
                        val actualizado = intent.agente.copy(estado = !intent.agente.estado)
                        saveAgenteUseCase(actualizado)
                    } catch (e: Exception) {
                        _state.update { it.copy(errorMessage = e.localizedMessage) }
                    }
                }
            }
        }
    }
}