package ucne.edu.rocash.presentation.agenteVentas.form

data class AgenteFormUiState(
    val isLoading: Boolean = false,
    val nombre: String = "",
    val telefono: String = "",
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)