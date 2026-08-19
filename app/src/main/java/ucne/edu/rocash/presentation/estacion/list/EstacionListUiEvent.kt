package ucne.edu.rocash.presentation.estacion.list

sealed interface EstacionListUiEvent {
    data object Load : EstacionListUiEvent
    data object Refresh : EstacionListUiEvent
    data class Delete(val id: Int) : EstacionListUiEvent
    data class ShowMessage(val message: String) : EstacionListUiEvent
    data object ClearMessage : EstacionListUiEvent
    data object CreateNew : EstacionListUiEvent
    data class Edit(val id: Int) : EstacionListUiEvent

    /**
     * La pantalla ya navego; apaga navigateToCreate y navigateToEditId.
     *
     * Sin esto, las banderas se quedan encendidas para siempre. Al volver de la
     * pantalla de formulario, la lista entra de nuevo en composicion, el
     * LaunchedEffect se vuelve a ejecutar con la bandera todavia en true y
     * rebota al usuario al formulario, dejandolo sin forma de salir.
     */
    data object NavegacionConsumida : EstacionListUiEvent
    data class SearchQueryChanged(val query: String) : EstacionListUiEvent
}