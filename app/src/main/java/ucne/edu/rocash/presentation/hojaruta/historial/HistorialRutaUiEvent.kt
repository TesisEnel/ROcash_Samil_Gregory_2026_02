package ucne.edu.rocash.presentation.hojaRuta.historial

sealed interface HistorialRutasUiEvent {
    data object CargarHistorial : HistorialRutasUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : HistorialRutasUiEvent
}
