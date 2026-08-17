package ucne.edu.rocash.presentation.auth

import com.google.firebase.auth.FirebaseUser

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)