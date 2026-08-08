package ucne.edu.rocash.presentation.agenteVentas.list

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

sealed interface AgenteListUiEvent {
    data class OnSearchQueryChange(val query: String) : AgenteListUiEvent
    data class ToggleEstadoAgente(val agente: AgenteVentas) : AgenteListUiEvent
}