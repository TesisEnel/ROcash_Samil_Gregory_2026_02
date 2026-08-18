package ucne.edu.rocash.presentation.hojaRuta.historial

import ucne.edu.rocash.presentation.core.UiEvent

sealed interface HistorialRutasUiEvent : UiEvent {
    data object CargarHistorial : HistorialRutasUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : HistorialRutasUiEvent
}
