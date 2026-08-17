package ucne.edu.rocash.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.auth.repository.AuthRepository
import ucne.edu.rocash.domain.auth.usecase.CheckSessionUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        val user = checkSessionUseCase()
        if (user != null) {
            _state.update {
                it.copy(
                    email = user.email ?: "Sin correo",
                    displayName = user.displayName ?: "Usuario ROcash"
                )
            }
        }
    }

    fun onEvent(event: ProfileUiEvent) {
        if (event is ProfileUiEvent.SignOut) {
            viewModelScope.launch {
                authRepository.signOut()
                _state.update { it.copy(isSignedOut = true) }
            }
        }
    }
}