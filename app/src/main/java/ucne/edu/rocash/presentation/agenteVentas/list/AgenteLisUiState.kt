package ucne.edu.rocash.presentation.agenteVentas.list

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

data class AgenteListUiState(
    val isLoading: Boolean = false,
    val agentes: List<AgenteVentas> = emptyList(),
    val message: String? = null,
    val navigateToCreate: Boolean = false,
    val navigateToEditId: Int? = null,
    val error: String? = null,
    val searchQuery: String = "",

    /**
     * Agente sobre el que se abrió el menú de acciones. Tocar la tarjeta ya no
     * lleva directo a editar: primero hay que elegir entre editar sus datos y
     * gestionar su deuda.
     */
    val agenteSeleccionado: AgenteVentas? = null,
    val navigateToDeudaId: Int? = null
)