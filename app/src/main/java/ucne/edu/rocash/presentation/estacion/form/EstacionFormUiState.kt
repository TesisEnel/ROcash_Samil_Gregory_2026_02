package ucne.edu.rocash.presentation.estacion.form

data class EstacionFormUiState(
    val nombre: String = "",
    val direccion: String = "",
    val agenteId: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)