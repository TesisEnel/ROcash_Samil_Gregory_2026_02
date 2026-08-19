package ucne.edu.rocash.presentation.auth.signup

sealed interface SignUpUiEvent {
    data class EmailChanged(val value: String) : SignUpUiEvent
    data class PasswordChanged(val value: String) : SignUpUiEvent
    data object SignUp : SignUpUiEvent
}
