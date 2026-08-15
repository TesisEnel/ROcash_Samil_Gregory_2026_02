package ucne.edu.rocash.presentation.profile

sealed interface ProfileUiEvent {
    data object SignOut : ProfileUiEvent
}