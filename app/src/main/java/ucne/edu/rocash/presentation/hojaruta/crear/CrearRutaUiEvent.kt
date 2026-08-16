package ucne.edu.rocash.presentation.hojaRuta.crear

sealed interface CrearRutaUiEvent {
    data object Load : CrearRutaUiEvent
    data class ToggleEstacion(val estacionId: Int) : CrearRutaUiEvent
    data object LimpiarSeleccion : CrearRutaUiEvent
    data object GenerarHojaRuta : CrearRutaUiEvent
    data object ErrorMostrado : CrearRutaUiEvent
}