package ucne.edu.rocash.presentation.agenteVentas.list

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

data class AgenteListUiState(
    val isLoading: Boolean = false,
    val agentes: List<AgenteVentas> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)