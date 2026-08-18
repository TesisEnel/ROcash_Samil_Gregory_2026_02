package ucne.edu.rocash.presentation.agenteVentas.list

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

sealed interface AgenteListUiEvent {
    data object Load : AgenteListUiEvent
    data object Refresh : AgenteListUiEvent
    data class Delete(val id: Int) : AgenteListUiEvent
    data class ShowMessage(val message: String) : AgenteListUiEvent
    data object ClearMessage : AgenteListUiEvent
    data object CreateNew : AgenteListUiEvent
    data class Edit(val id: Int) : AgenteListUiEvent

    /** Tocar la tarjeta abre el menú de acciones. */
    data class AgenteTocado(val agente: AgenteVentas) : AgenteListUiEvent
    data object CerrarAcciones : AgenteListUiEvent
    data class GestionarDeuda(val id: Int) : AgenteListUiEvent

    /**
     * La pantalla ya navego; apaga navigateToCreate y navigateToEditId.
     *
     * Sin esto, las banderas se quedan encendidas para siempre. Al volver de la
     * pantalla de formulario, la lista entra de nuevo en composicion, el
     * LaunchedEffect se vuelve a ejecutar con la bandera todavia en true y
     * rebota al usuario al formulario, dejandolo sin forma de salir.
     */
    data object NavegacionConsumida : AgenteListUiEvent
    data class SearchQueryChanged(val query: String) : AgenteListUiEvent
    data class ToggleEstado(val agente: AgenteVentas) : AgenteListUiEvent
}