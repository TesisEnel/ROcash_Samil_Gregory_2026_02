package ucne.edu.rocash.presentation.estacion.detalle

sealed interface EstacionDetalleUiEvent {
    data class Load(val estacionId: Int) : EstacionDetalleUiEvent
    data object ErrorMostrado : EstacionDetalleUiEvent
}
