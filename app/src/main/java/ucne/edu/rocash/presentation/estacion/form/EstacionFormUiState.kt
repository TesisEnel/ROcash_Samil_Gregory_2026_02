package ucne.edu.rocash.presentation.estacion.form

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

data class EstacionFormUiState(
    val nombre: String = "",
    val direccion: String = "",
    val agenteId: String = "",
    val agenteNombreSeleccionado: String = "",
    val agentesDisponibles: List<AgenteVentas> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)