package ucne.edu.rocash.presentation.estacion.list

import ucne.edu.rocash.domain.model.EstacionVentas

data class EstacionListUiState(
    val isLoading: Boolean = true,
    val estaciones: List<EstacionVentas> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)