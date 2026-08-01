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
import ucne.edu.rocash.domain.auth.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        authRepository.getCurrentUser()?.let { user ->
            _state.update { it.copy(user = user) }
        }
    }

    fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignInWithGoogle -> signIn(intent.context)
            is AuthIntent.SignInWithEmail -> signInEmail(intent.email, intent.password)
            is AuthIntent.SignOut -> signOut()
        }
    }

    private fun signIn(context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signInWithGoogle(context)

            result.fold(
                onSuccess = { user -> _state.update { it.copy(isLoading = false, user = user) } },
                onFailure = { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) } }
            )
        }
    }

    private fun signInEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(errorMessage = "Llena todos los campos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signInWithEmail(email, password)

            result.fold(
                onSuccess = { user -> _state.update { it.copy(isLoading = false, user = user) } },
                onFailure = { e -> _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) } }
            )
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _state.update { it.copy(user = null) }
        }
    }
}