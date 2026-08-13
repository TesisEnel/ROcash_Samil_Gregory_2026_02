package ucne.edu.rocash.presentation.ruta

sealed interface CrearRutaUIEvent {
    data object Load : CrearRutaUIEvent
    data class ToggleEstacionSeleccionada(val id: Int) : CrearRutaUIEvent
    data object GenerarHojaRuta : CrearRutaUIEvent
}