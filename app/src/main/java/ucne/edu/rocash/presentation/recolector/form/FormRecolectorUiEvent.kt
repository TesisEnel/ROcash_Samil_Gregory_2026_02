package ucne.edu.rocash.presentation.recolector.form

sealed interface FormRecolectorUiEvent {
    data class OnNombreChange(val nombre: String) : FormRecolectorUiEvent
    data class OnTelefonoChange(val telefono: String) : FormRecolectorUiEvent
    object GuardarRecolector : FormRecolectorUiEvent
    object ResetSuccessState : FormRecolectorUiEvent
}