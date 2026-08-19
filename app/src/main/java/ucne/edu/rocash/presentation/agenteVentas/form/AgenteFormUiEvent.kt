package ucne.edu.rocash.presentation.agenteVentas.form

sealed interface AgenteFormUiEvent {
    data class Load(val id: Int?) : AgenteFormUiEvent
    data class NombreChanged(val value: String) : AgenteFormUiEvent
    data class TelefonoChanged(val value: String) : AgenteFormUiEvent
    data object Save : AgenteFormUiEvent
    data object Delete : AgenteFormUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : AgenteFormUiEvent
}