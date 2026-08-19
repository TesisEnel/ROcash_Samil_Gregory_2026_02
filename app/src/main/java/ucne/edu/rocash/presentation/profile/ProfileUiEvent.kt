package ucne.edu.rocash.presentation.profile

sealed interface ProfileUiEvent {
    data object SignOut : ProfileUiEvent
    data object ToggleEditMode : ProfileUiEvent
    data class NameChanged(val value: String) : ProfileUiEvent
    data class PhoneChanged(val value: String) : ProfileUiEvent
    data object SaveProfile : ProfileUiEvent
}