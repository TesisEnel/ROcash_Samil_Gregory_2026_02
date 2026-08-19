package ucne.edu.rocash.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.auth.usecase.SignUpWithEmailUseCase
import ucne.edu.rocash.domain.auth.usecase.validateEmail
import ucne.edu.rocash.domain.auth.usecase.validatePassword
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SignUpUiState())
    val state: StateFlow<SignUpUiState> = _state.asStateFlow()

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.EmailChanged -> _state.update { it.copy(email = event.value, emailError = null) }
            is SignUpUiEvent.PasswordChanged -> _state.update { it.copy(password = event.value, passwordError = null) }
            SignUpUiEvent.SignUp -> signUp()
        }
    }

    private fun signUp() {
        val currentState = _state.value
        val emailValidation = validateEmail(currentState.email)
        val passwordValidation = validatePassword(currentState.password)

        if (!emailValidation.isValid || !passwordValidation.isValid) {
            _state.update { it.copy(emailError = emailValidation.error, passwordError = passwordValidation.error) }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = signUpWithEmailUseCase(currentState.email, currentState.password)
            result.onSuccess {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}
