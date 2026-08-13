package ucne.edu.rocash.presentation.agenteVentas.list

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

sealed interface AgenteListUiEvent {
    data object Load : AgenteListUiEvent
    data object Refresh : AgenteListUiEvent
    data class Delete(val id: Int) : AgenteListUiEvent
    data class ShowMessage(val message: String) : AgenteListUiEvent
    data object ClearMessage : AgenteListUiEvent
    data object CreateNew : AgenteListUiEvent
    data class Edit(val id: Int) : AgenteListUiEvent
    data class SearchQueryChanged(val query: String) : AgenteListUiEvent
    data class ToggleEstado(val agente: AgenteVentas) : AgenteListUiEvent
}