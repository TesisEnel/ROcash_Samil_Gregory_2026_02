package ucne.edu.rocash.presentation.auth

import android.content.Context

sealed class AuthIntent {
    data class SignInWithGoogle(val context: Context) : AuthIntent()
    data class SignInWithEmail(val email: String, val password: String) : AuthIntent()
    object SignOut : AuthIntent()
}