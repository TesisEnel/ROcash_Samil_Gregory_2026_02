package ucne.edu.rocash.presentation.home

sealed class HomeUIEvent {
    object CargarDatos : HomeUIEvent()
    object CrearRutaPrueba : HomeUIEvent()
}