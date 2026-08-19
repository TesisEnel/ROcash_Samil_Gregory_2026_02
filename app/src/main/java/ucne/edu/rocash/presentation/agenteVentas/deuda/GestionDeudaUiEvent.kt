package ucne.edu.rocash.presentation.agenteVentas.deuda

sealed interface GestionDeudaUiEvent {
    data class Load(val agenteId: Int) : GestionDeudaUiEvent
    data class MontoChanged(val value: String) : GestionDeudaUiEvent
    data class NotaChanged(val value: String) : GestionDeudaUiEvent

    data object Abonar : GestionDeudaUiEvent

    data object PedirConfirmacionSaldar : GestionDeudaUiEvent
    data object CancelarSaldar : GestionDeudaUiEvent
    data object Saldar : GestionDeudaUiEvent

    data object MensajeMostrado : GestionDeudaUiEvent
    data object ErrorMostrado : GestionDeudaUiEvent
}
