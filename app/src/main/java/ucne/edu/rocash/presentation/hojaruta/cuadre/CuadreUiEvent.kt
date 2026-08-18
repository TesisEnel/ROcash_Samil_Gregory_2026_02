package ucne.edu.rocash.presentation.hojaRuta.cuadre

import ucne.edu.rocash.presentation.core.UiEvent

sealed interface CuadreUiEvent : UiEvent {
    data class Load(
        val hojaRutaId: Int,
        val estacionId: Int,
        val agenteId: Int,
        val nombre: String
    ) : CuadreUiEvent

    data class VentaBrutaChanged(val value: String) : CuadreUiEvent
    data class ComisionChanged(val value: String) : CuadreUiEvent
    data class MontoRecolectadoChanged(val value: String) : CuadreUiEvent
    data class NotaChanged(val value: String) : CuadreUiEvent

    data object Save : CuadreUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : CuadreUiEvent
}
