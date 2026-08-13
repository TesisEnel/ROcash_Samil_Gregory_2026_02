package ucne.edu.rocash.presentation.estacion.list

import ucne.edu.rocash.domain.estacion.model.EstacionVentas

data class EstacionListUiState(
    val isLoading: Boolean = false,
    val estaciones: List<EstacionVentas> = emptyList(),
    val message: String? = null,
    val navigateToCreate: Boolean = false,
    val navigateToEditId: Int? = null,
    val error: String? = null,
    val searchQuery: String = ""
)