package ucne.edu.rocash.presentation.estacion.form

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

data class EstacionFormUiState(
    val estacionId: Int? = null,
    val nombre: String = "",
    val direccion: String = "",
    val agenteId: Int? = null,
    val agenteNombreSeleccionado: String = "",

    /**
     * Segundo agente de la banca. El formulario no lo edita, pero se carga y se
     * devuelve tal cual al guardar.
     *
     * Antes onSave() construía el EstacionVentas sin este campo, así que editar
     * el nombre de una banca de dos agentes le borraba el segundo. El daño no
     * era solo perder un dato: `deudaSeReparte` en el cuadre depende de él, de
     * modo que después de editar, la deuda de esa banca dejaba de repartirse y
     * se le cargaba completa a un solo agente.
     */
    val agenteId2: Int? = null,
    val agentesDisponibles: List<AgenteVentas> = emptyList(),

    val nombreError: String? = null,
    val direccionError: String? = null,
    val agenteError: String? = null,

    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val errorMessage: String? = null
)