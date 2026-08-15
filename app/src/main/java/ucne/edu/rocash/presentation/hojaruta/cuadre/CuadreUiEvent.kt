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

    data object Save : CuadreUiEvent
    data object ErrorMostrado : CuadreUiEvent
}