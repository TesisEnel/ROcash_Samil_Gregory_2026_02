package ucne.edu.rocash.presentation.hojaRuta.crear

import ucne.edu.rocash.presentation.core.UiEvent

sealed interface CrearRutaUiEvent : UiEvent {
    data object Load : CrearRutaUiEvent
    data class ToggleEstacion(val estacionId: Int) : CrearRutaUiEvent
    data object LimpiarSeleccion : CrearRutaUiEvent
    data object GenerarHojaRuta : CrearRutaUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : CrearRutaUiEvent
}
