package ucne.edu.rocash.presentation.home

import ucne.edu.rocash.presentation.core.UiEvent

sealed interface HomeUiEvent : UiEvent {
    data object CargarDatos : HomeUiEvent

    /** La pantalla ya mostró el snackbar; apaga la bandera. */
    data object ErrorMostrado : HomeUiEvent
}
