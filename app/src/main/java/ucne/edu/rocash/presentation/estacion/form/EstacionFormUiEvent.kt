package ucne.edu.rocash.presentation.estacion.form

sealed interface EstacionFormUiEvent {
    data class OnNombreChange(val value: String) : EstacionFormUiEvent
    data class OnDireccionChange(val value: String) : EstacionFormUiEvent
    data class OnAgenteSeleccionado(val id: String, val nombre: String) : EstacionFormUiEvent
    object GuardarEstacion : EstacionFormUiEvent
    object ResetSuccessState : EstacionFormUiEvent
}