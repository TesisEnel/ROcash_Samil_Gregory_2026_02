package ucne.edu.rocash.presentation.detalle

data class DetalleUIState(
    val isLoading: Boolean = false,
    val hojaRutaId: Int = 0,
    val estacionId: String = "",
    val agenteId: String = "",
    val nombreEstacion: String = "",
    val ventaBruta: String = "",
    val comisionCliente: String = "",
    val montoRecolectado: String = "",
    val montoEsperado: Double = 0.0,
    val deudaGenerada: Double = 0.0,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)