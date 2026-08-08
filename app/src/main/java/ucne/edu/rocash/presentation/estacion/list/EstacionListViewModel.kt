package ucne.edu.rocash.presentation.estacion.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.model.EstacionVentas
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

@HiltViewModel
class EstacionListViewModel @Inject constructor(
    private val repository: RoCashRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EstacionListUiState())
    val state: StateFlow<EstacionListUiState> = _state.asStateFlow()

    private var todasLasEstaciones: List<EstacionVentas> = emptyList()

    init {
        processIntent(EstacionListUiEvent.CargarEstaciones)
    }

    fun processIntent(intent: EstacionListUiEvent) {
        when (intent) {
            is EstacionListUiEvent.CargarEstaciones -> cargarEstaciones()
            is EstacionListUiEvent.OnSearchQueryChange -> filtrarEstaciones(intent.query)
        }
    }

    private fun cargarEstaciones() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                repository.observeAllEstaciones().collectLatest { lista ->
                    todasLasEstaciones = lista
                    filtrarEstaciones(_state.value.searchQuery)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    private fun filtrarEstaciones(query: String) {
        val filtradas = if (query.isBlank()) {
            todasLasEstaciones
        } else {
            todasLasEstaciones.filter {
                it.nombre.contains(query, ignoreCase = true) || it.direccion.contains(query, ignoreCase = true)
            }
        }

        _state.update {
            it.copy(
                searchQuery = query,
                estaciones = filtradas,
                isLoading = false
            )
        }
    }
}