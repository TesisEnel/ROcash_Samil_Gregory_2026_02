package ucne.edu.rocash.presentation.estacion.form

sealed interface EstacionFormUiEvent {
    data class Load(val id: Int?) : EstacionFormUiEvent
    data class NombreChanged(val value: String) : EstacionFormUiEvent
    data class DireccionChanged(val value: String) : EstacionFormUiEvent
    data class AgenteSeleccionado(val id: Int, val nombre: String) : EstacionFormUiEvent
    data object Save : EstacionFormUiEvent
    data object Delete : EstacionFormUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : EstacionFormUiEvent
}