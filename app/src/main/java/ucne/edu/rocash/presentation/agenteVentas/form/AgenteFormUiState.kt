package ucne.edu.rocash.presentation.agenteVentas.form

data class AgenteFormUiState(
    val agenteId: Int? = null,
    val nombre: String = "",
    val telefono: String = "",

    /**
     * Se cargan del agente existente y se devuelven tal cual al guardar.
     *
     * Antes el formulario construía el AgenteVentas con `deudaAcumulada = 0.0`
     * y `estado = true` fijos, así que editar el teléfono de un agente le
     * borraba la deuda acumulada y reactivaba a uno dado de baja. El formulario
     * no edita ninguno de los dos campos, así que lo único correcto es
     * conservarlos.
     */
    val deudaAcumulada: Double = 0.0,
    val estado: Boolean = true,

    val nombreError: String? = null,
    val telefonoError: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val errorMessage: String? = null
)