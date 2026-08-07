package ucne.edu.rocash.presentation.recolector.list

import ucne.edu.rocash.domain.recolector.model.Recolector

sealed interface ListRecolectorUiEvent {
    object CargarRecolectores : ListRecolectorUiEvent
    data class OnSearchQueryChange(val query: String) : ListRecolectorUiEvent
    data class ToggleEstadoRecolector(val recolector: Recolector) : ListRecolectorUiEvent
}