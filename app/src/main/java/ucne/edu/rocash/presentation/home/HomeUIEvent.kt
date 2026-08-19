package ucne.edu.rocash.presentation.home

sealed interface HomeUiEvent {
    data object CargarDatos : HomeUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : HomeUiEvent
}
