package ucne.edu.rocash.presentation.hojaRuta.detalle

sealed interface DetalleRutaUiEvent {
    data class Load(val rutaId: Int) : DetalleRutaUiEvent
    data object PedirConfirmacionCierre : DetalleRutaUiEvent
    data object CancelarCierre : DetalleRutaUiEvent
    data object ConfirmarCierre : DetalleRutaUiEvent
    data class OmitirEstacion(val estacionId: Int) : DetalleRutaUiEvent
    data object ErrorMostrado : DetalleRutaUiEvent
}