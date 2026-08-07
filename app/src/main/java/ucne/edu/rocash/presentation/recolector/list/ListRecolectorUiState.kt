package ucne.edu.rocash.presentation.recolector.list

import ucne.edu.rocash.domain.recolector.model.Recolector

data class ListRecolectorUiState(
    val isLoading: Boolean = false,
    val recolectores: List<Recolector> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)