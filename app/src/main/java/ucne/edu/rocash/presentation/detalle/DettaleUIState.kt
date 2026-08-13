package ucne.edu.rocash.presentation.detalle

data class DetalleUiState(
    val hojaRutaId: Int = 0,
    val estacionId: Int = 0,
    val agenteId: Int = 0,
    val nombreEstacion: String = "",

    val ventaBruta: String = "",
    val comisionCliente: String = "",
    val montoRecolectado: String = "",

    val montoEsperado: Double = 0.0,
    val deudaGenerada: Double = 0.0,

    val ventaBrutaError: String? = null,
    val comisionError: String? = null,
    val montoRecolectadoError: String? = null,

    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val errorMessage: String? = null
)