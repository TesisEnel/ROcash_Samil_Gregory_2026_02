package ucne.edu.rocash.presentation.profile

data class ProfileUiState(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phone: String = "",
    val isEditing: Boolean = false,
    val isSignedOut: Boolean = false,
    val photoUrl: String? = null
)
