package ucne.edu.rocash.presentation.detalle

data class DetalleUIState(
    val isLoading: Boolean = false,
    val estacionId: String = "",
    val agenteId: String = "",
    val nombreEstacion: String = "",
    val ventaBruta: String = "",
    val porcentajeCliente: String = "",
    val montoRecolectado: String = "",
    val montoEsperado: Double = 0.0, // Venta Bruta - Porcentaje
    val deudaGenerada: Double = 0.0,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)