package ucne.edu.rocash.presentation.hojaRuta.cuadre

sealed interface CuadreUiEvent {
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

    /**
     * El usuario tocó el botón. Valida y, si todo cuadra, pide confirmación.
     * NO guarda: marcar una banca como recolectada mueve dinero y deuda de un
     * agente, así que no debe ocurrir por un toque accidental.
     */
    data object PedirConfirmacion : CuadreUiEvent

    data object CancelarConfirmacion : CuadreUiEvent

    /** Confirmado en el diálogo. Este sí persiste. */
    data object Save : CuadreUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : CuadreUiEvent
}
