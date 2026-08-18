package ucne.edu.rocash.presentation.hojaRuta.crear

sealed interface CrearRutaUiEvent {
    data object Load : CrearRutaUiEvent
    data class ToggleEstacion(val estacionId: Int) : CrearRutaUiEvent
    data object LimpiarSeleccion : CrearRutaUiEvent
    data object GenerarHojaRuta : CrearRutaUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : CrearRutaUiEvent
}
