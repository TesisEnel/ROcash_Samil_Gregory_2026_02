package ucne.edu.rocash.presentation.agenteVentas.form

data class AgenteFormUiState(
    val agenteId: Int? = null,
    val nombre: String = "",
    val telefono: String = "",

    val deudaAcumulada: Double = 0.0,
    val estado: Boolean = true,

    val nombreError: String? = null,
    val telefonoError: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val errorMessage: String? = null
)
