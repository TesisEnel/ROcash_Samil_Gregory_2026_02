package ucne.edu.rocash.presentation.home

sealed interface HomeUiEvent {
    data object CargarDatos : HomeUiEvent
    data object ErrorMostrado : HomeUiEvent
}
