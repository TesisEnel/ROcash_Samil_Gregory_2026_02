package ucne.edu.rocash.presentation.auth

import android.content.Context

sealed interface AuthUiEvent {
    data class EmailChanged(val value: String) : AuthUiEvent
    data class PasswordChanged(val value: String) : AuthUiEvent
    data class SignInWithGoogle(val context: Context) : AuthUiEvent
    data object SignInWithEmail : AuthUiEvent
}