package ucne.edu.rocash.presentation.agenteVentas.form

sealed interface AgenteFormUiEvent {
    data class OnNombreChange(val nombre: String) : AgenteFormUiEvent
    data class OnTelefonoChange(val telefono: String) : AgenteFormUiEvent
    object GuardarAgente : AgenteFormUiEvent
    object ResetSuccessState : AgenteFormUiEvent
}