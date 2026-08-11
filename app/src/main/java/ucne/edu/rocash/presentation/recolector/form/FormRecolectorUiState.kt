package ucne.edu.rocash.presentation.recolector.form

data class FormRecolectorUiState(
    val isLoading: Boolean = false,
    val nombre: String = "",
    val telefono: String = "",
    val cedula: String= "",
    val recolectorId: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val nombreError: String? = null,
    val telefonoError: String? = null,
    val cedulaError: String? = null
)