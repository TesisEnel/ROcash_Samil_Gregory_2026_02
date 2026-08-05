package ucne.edu.rocash.presentation.estacion

sealed class CrearEstacionUIEvent {
    data class OnNombreChange(val value: String) : CrearEstacionUIEvent()
    data class OnDireccionChange(val value: String) : CrearEstacionUIEvent()
    data class OnAgenteIdChange(val value: String) : CrearEstacionUIEvent()
    object GuardarEstacion : CrearEstacionUIEvent()
}