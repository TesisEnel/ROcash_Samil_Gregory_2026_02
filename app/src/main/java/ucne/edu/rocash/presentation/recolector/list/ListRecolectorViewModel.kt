package ucne.edu.rocash.presentation.recolector.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.recolector.usecase.SaveRecolectorUseCase
import ucne.edu.rocash.domain.recolector.usecase.GetRecolectoresUseCase
import ucne.edu.rocash.domain.recolector.usecase.SearchRecolectoresUseCase
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ListRecolectorViewModel @Inject constructor(
    private val getRecolectoresUseCase: GetRecolectoresUseCase,
    private val searchRecolectoresUseCase: SearchRecolectoresUseCase,
    private val saveRecolectorUseCase: SaveRecolectorUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListRecolectorUiState())
    val state: StateFlow<ListRecolectorUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        getRecolectoresUseCase()
                    } else {
                        searchRecolectoresUseCase(query)
                    }
                }
                .collectLatest { lista ->
                    _state.update {
                        it.copy(isLoading = false, recolectores = lista, errorMessage = null)
                    }
                }
        }
    }

    fun processIntent(intent: ListRecolectorUiEvent) {
        when (intent) {
            is ListRecolectorUiEvent.CargarRecolectores -> {
                _state.update { it.copy(isLoading = true) }
            }
            is ListRecolectorUiEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
            is ListRecolectorUiEvent.ToggleEstadoRecolector -> {
                viewModelScope.launch {
                    try {
                        val actualizado = intent.recolector.copy(estado = !intent.recolector.estado)
                        saveRecolectorUseCase(actualizado)
                    } catch (e: Exception) {
                        _state.update { it.copy(errorMessage = e.localizedMessage) }
                    }
                }
            }
        }
    }
}