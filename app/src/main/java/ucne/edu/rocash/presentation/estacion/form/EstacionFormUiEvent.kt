package ucne.edu.rocash.presentation.estacion.form

sealed class EstacionFormUiEvent {
    data class OnNombreChange(val value: String) : EstacionFormUiEvent()
    data class OnDireccionChange(val value: String) : EstacionFormUiEvent()
    data class OnAgenteIdChange(val value: String) : EstacionFormUiEvent()
    object GuardarEstacion : EstacionFormUiEvent()
}