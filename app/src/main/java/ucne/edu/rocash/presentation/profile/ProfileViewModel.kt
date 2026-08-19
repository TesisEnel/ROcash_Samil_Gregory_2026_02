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
import ucne.edu.rocash.domain.recolector.model.Recolector
import ucne.edu.rocash.domain.recolector.repository.RecolectorRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val authRepository: AuthRepository,
    private val recolectorRepository: RecolectorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        cargarPerfil()
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.SignOut -> {
                viewModelScope.launch {
                    authRepository.signOut()
                    _state.update { it.copy(isSignedOut = true) }
                }
            }
            ProfileUiEvent.ToggleEditMode -> {
                _state.update { it.copy(isEditing = !it.isEditing) }
            }
            is ProfileUiEvent.NameChanged -> {
                _state.update { it.copy(displayName = event.value) }
            }
            is ProfileUiEvent.PhoneChanged -> {
                _state.update { it.copy(phone = event.value) }
            }
            ProfileUiEvent.SaveProfile -> {
                viewModelScope.launch {
                    val currentState = _state.value

                    val perfilActualizado = Recolector(
                        id = currentState.uid,
                        nombre = currentState.displayName,
                        telefono = currentState.phone,
                        cedula = "",
                        estado = true
                    )

                    recolectorRepository.insertarRecolector(perfilActualizado)
                    _state.update { it.copy(isEditing = false) }
                }
            }
        }
    }

    private fun cargarPerfil() {
        val user = checkSessionUseCase()
        if (user != null) {
            _state.update {
                it.copy(
                    uid = user.uid,
                    email = user.email ?: "Sin correo",
                    displayName = user.displayName ?: "Usuario ROcash",
                    photoUrl = user.photoUrl?.toString()
                )
            }

            viewModelScope.launch {
                val perfilLocal = recolectorRepository.obtenerRecolectorPorId(user.uid)
                if (perfilLocal != null) {
                    _state.update {
                        it.copy(
                            displayName = perfilLocal.nombre.ifBlank { user.displayName ?: "" },
                            phone = perfilLocal.telefono
                        )
                    }
                }
            }
        }
    }

}