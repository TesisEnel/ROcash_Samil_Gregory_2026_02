package ucne.edu.rocash.presentation.estacion.list

sealed class EstacionListUiEvent {
    object CargarEstaciones : EstacionListUiEvent()
    data class OnSearchQueryChange(val query: String) : EstacionListUiEvent()
}