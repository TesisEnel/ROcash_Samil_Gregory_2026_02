package ucne.edu.rocash.presentation.ruta

sealed class CrearRutaUIEvent {
    object CargarEstaciones : CrearRutaUIEvent()
    data class ToggleEstacionSeleccionada(val estacionId: String) : CrearRutaUIEvent()
    object GenerarHojaRuta : CrearRutaUIEvent()
}