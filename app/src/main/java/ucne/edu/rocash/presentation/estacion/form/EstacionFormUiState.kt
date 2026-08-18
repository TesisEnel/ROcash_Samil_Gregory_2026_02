package ucne.edu.rocash.presentation.estacion.form

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

data class EstacionFormUiState(
    val estacionId: Int? = null,
    val nombre: String = "",
    val direccion: String = "",
    val agenteId: Int? = null,
    val agenteNombreSeleccionado: String = "",
    val agentesDisponibles: List<AgenteVentas> = emptyList(),

    val nombreError: String? = null,
    val direccionError: String? = null,
    val agenteError: String? = null,

    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val errorMessage: String? = null
)