package ucne.edu.rocash.presentation.hojaRuta.historial

sealed interface HistorialRutasUiEvent {
    data object CargarHistorial : HistorialRutasUiEvent
    data object ErrorMostrado : HistorialRutasUiEvent
}