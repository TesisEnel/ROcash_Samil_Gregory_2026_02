package ucne.edu.rocash.presentation.recolector.form

sealed interface FormRecolectorUiEvent {
    data class Inicializar(val id: String?) : FormRecolectorUiEvent
    data class OnNombreChange(val nombre: String) : FormRecolectorUiEvent
    data class OnTelefonoChange(val telefono: String) : FormRecolectorUiEvent

    data class OnCedulaChange(val cedula: String): FormRecolectorUiEvent
    object GuardarRecolector : FormRecolectorUiEvent
    object ResetSuccessState : FormRecolectorUiEvent
}