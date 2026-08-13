package ucne.edu.rocash.presentation.agenteVentas.list

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

data class AgenteListUiState(
    val isLoading: Boolean = false,
    val agentes: List<AgenteVentas> = emptyList(),
    val message: String? = null,
    val navigateToCreate: Boolean = false,
    val navigateToEditId: Int? = null,
    val error: String? = null,
    val searchQuery: String = ""
)