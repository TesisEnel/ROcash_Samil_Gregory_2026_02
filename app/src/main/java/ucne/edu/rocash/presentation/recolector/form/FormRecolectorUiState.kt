package ucne.edu.rocash.presentation.recolector.form

data class FormRecolectorUiState(
    val isLoading: Boolean = false,
    val nombre: String = "",
    val telefono: String = "",
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)