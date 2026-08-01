package ucne.edu.rocash.presentation.auth

import com.google.firebase.auth.FirebaseUser

// 1. ESTADO DE LA PANTALLA
data class AuthState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val errorMessage: String? = null
)