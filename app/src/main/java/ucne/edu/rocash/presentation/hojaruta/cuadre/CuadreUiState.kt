package ucne.edu.rocash.presentation.hojaRuta.cuadre
data class CuadreUiState(
    val hojaRutaId: Int = 0,
    val estacionId: Int = 0,
    val agenteId: Int = 0,
    val agenteId2: Int? = null,
    val nombreEstacion: String = "",

    val ventaBruta: String = "",
    val comisionCliente: String = "",
    val montoRecolectado: String = "",
    val notaIncidencia: String = "",

    val montoEsperado: Double = 0.0,
    val deudaGenerada: Double = 0.0,
    val hayDeuda: Boolean = false,
    val deudaSeReparte: Boolean = false,

    val ventaBrutaError: String? = null,
    val comisionError: String? = null,
    val montoRecolectadoError: String? = null,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isNew: Boolean = true,
    val puedeGuardar: Boolean = false,

    val mostrarDialogoConfirmacion: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null
)
