package ucne.edu.rocash.presentation.profile

data class ProfileUiState(
    val email: String = "",
    val displayName: String = "Usuario",
    val isSignedOut: Boolean = false
)