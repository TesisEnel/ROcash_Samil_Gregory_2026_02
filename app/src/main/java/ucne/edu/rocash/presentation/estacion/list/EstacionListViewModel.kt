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
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.usecase.GetEstacionesUseCase
import ucne.edu.rocash.domain.estacion.usecase.SearchEstacionesUseCase
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EstacionListViewModel @Inject constructor(
    private val getEstacionesUseCase: GetEstacionesUseCase,
    private val searchEstacionesUseCase: SearchEstacionesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EstacionListUiState())
    val state: StateFlow<EstacionListUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        getEstacionesUseCase()
                    } else {
                        searchEstacionesUseCase(query)
                    }
                }
                .collectLatest { lista ->
                    _state.update {
                        it.copy(isLoading = false, estaciones = lista, errorMessage = null)
                    }
                }
        }
    }

    fun processIntent(intent: EstacionListUiEvent) {
        when (intent) {
            is EstacionListUiEvent.CargarEstaciones -> {
                _state.update { it.copy(isLoading = true) }
            }
            is EstacionListUiEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
        }
    }
}