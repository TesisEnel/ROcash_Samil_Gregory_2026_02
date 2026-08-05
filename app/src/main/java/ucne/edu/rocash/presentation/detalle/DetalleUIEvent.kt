package ucne.edu.rocash.presentation.detalle

sealed class DetalleUIEvent {
    data class Inicializar(val estacionId: String, val agenteId: String, val nombre: String) : DetalleUIEvent()
    data class OnVentaBrutaChange(val value: String) : DetalleUIEvent()
    data class OnPorcentajeChange(val value: String) : DetalleUIEvent()
    data class OnMontoRecolectadoChange(val value: String) : DetalleUIEvent()
    object ProcesarRecoleccion : DetalleUIEvent()
}