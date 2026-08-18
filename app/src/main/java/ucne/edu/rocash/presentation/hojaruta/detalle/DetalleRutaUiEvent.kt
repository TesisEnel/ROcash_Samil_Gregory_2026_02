package ucne.edu.rocash.presentation.hojaRuta.detalle

import ucne.edu.rocash.presentation.core.UiEvent

sealed interface DetalleRutaUiEvent : UiEvent {
    data class Load(val rutaId: Int) : DetalleRutaUiEvent
    data object PedirConfirmacionCierre : DetalleRutaUiEvent
    data object CancelarCierre : DetalleRutaUiEvent
    data object ConfirmarCierre : DetalleRutaUiEvent
    data class OmitirEstacion(val estacionId: Int) : DetalleRutaUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : DetalleRutaUiEvent
}
