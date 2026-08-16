package ucne.edu.rocash.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.auth.usecase.CheckSessionUseCase
import ucne.edu.rocash.domain.auth.usecase.SignInWithEmailUseCase
import ucne.edu.rocash.domain.auth.usecase.SignInWithGoogleUseCase
import ucne.edu.rocash.domain.auth.usecase.validateEmail
import ucne.edu.rocash.domain.auth.usecase.validatePassword
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        val user = checkSessionUseCase()
        if (user != null) {
            _state.update { it.copy(user = user, isSuccess = true) }
        }
    }

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.EmailChanged -> _state.update { it.copy(email = event.value, emailError = null, errorMessage = null) }
            is AuthUiEvent.PasswordChanged -> _state.update { it.copy(password = event.value, passwordError = null, errorMessage = null) }
            is AuthUiEvent.SignInWithEmail -> signInEmail()
            is AuthUiEvent.SignInWithGoogle -> signInGoogle(event.context)
        }
    }

    private fun signInEmail() {
        val currentState = _state.value

        val emailValidation = validateEmail(currentState.email)
        val passwordValidation = validatePassword(currentState.password)

        if (!emailValidation.isValid || !passwordValidation.isValid) {
            _state.update {
                it.copy(
                    emailError = emailValidation.error,
                    passwordError = passwordValidation.error
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = signInWithEmailUseCase(currentState.email, currentState.password)

            result.onSuccess { user ->
                _state.update { it.copy(isLoading = false, user = user, isSuccess = true) }
            }.onFailure { e ->
                val msg = if (e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true) {
                    "Correo o contraseña incorrectos"
                } else {
                    e.localizedMessage ?: "Error desconocido"
                }
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    private fun signInGoogle(context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = signInWithGoogleUseCase(context)

            result.onSuccess { user ->
                _state.update { it.copy(isLoading = false, user = user, isSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}