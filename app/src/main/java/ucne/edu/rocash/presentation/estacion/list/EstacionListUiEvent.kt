package ucne.edu.rocash.presentation.estacion.list

sealed interface EstacionListUiEvent {
    data object Load : EstacionListUiEvent
    data object Refresh : EstacionListUiEvent
    data class Delete(val id: Int) : EstacionListUiEvent
    data class ShowMessage(val message: String) : EstacionListUiEvent
    data object ClearMessage : EstacionListUiEvent
    data object CreateNew : EstacionListUiEvent
    data class Edit(val id: Int) : EstacionListUiEvent
    data object NavegacionConsumida : EstacionListUiEvent
    data class SearchQueryChanged(val query: String) : EstacionListUiEvent
}