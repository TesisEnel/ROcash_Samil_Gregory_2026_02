package ucne.edu.rocash.presentation.detalle

sealed interface DetalleUiEvent {
    data class Load(
        val hojaRutaId: Int,
        val estacionId: Int,
        val agenteId: Int,
        val nombre: String
    ) : DetalleUiEvent

    data class VentaBrutaChanged(val value: String) : DetalleUiEvent
    data class ComisionChanged(val value: String) : DetalleUiEvent
    data class MontoRecolectadoChanged(val value: String) : DetalleUiEvent

    data object Save : DetalleUiEvent
}