package ucne.edu.rocash.presentation.estacion.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.estacion.usecase.DeleteEstacionUseCase
import ucne.edu.rocash.domain.estacion.usecase.ObserveEstacionesUseCase
import ucne.edu.rocash.domain.estacion.usecase.SearchEstacionesUseCase
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EstacionListViewModel @Inject constructor(
    private val observeEstacionesUseCase: ObserveEstacionesUseCase,
    private val searchEstacionesUseCase: SearchEstacionesUseCase,
    private val deleteEstacionUseCase: DeleteEstacionUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(EstacionListUiState(isLoading = true))
    val state: StateFlow<EstacionListUiState> = _state.asStateFlow()

    init {
        loadEstaciones()
    }

    fun onEvent(event: EstacionListUiEvent) {
        when (event) {
            EstacionListUiEvent.Load -> loadEstaciones()
            EstacionListUiEvent.Refresh -> loadEstaciones()
            is EstacionListUiEvent.Delete -> onDelete(event.id)
            is EstacionListUiEvent.ShowMessage -> _state.update { it.copy(message = event.message) }
            EstacionListUiEvent.ClearMessage -> _state.update { it.copy(message = null) }
            EstacionListUiEvent.CreateNew -> _state.update { it.copy(navigateToCreate = true) }
            is EstacionListUiEvent.Edit -> _state.update { it.copy(navigateToEditId = event.id) }

            EstacionListUiEvent.NavegacionConsumida -> _state.update {
                it.copy(navigateToCreate = false, navigateToEditId = null)
            }
            is EstacionListUiEvent.SearchQueryChanged -> _state.update { it.copy(searchQuery = event.query) }
        }
    }

    private fun loadEstaciones() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            _state.map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) observeEstacionesUseCase() else searchEstacionesUseCase(query)
                }
                .collectLatest { lista ->
                    _state.update { it.copy(isLoading = false, estaciones = lista, message = null) }
                }
        }
    }

    private fun onDelete(id: Int) {
        viewModelScope.launch {
            deleteEstacionUseCase(id)
            onEvent(EstacionListUiEvent.ShowMessage("Estación eliminada"))
        }
    }
}
