package ucne.edu.rocash.domain.model

data class AgenteVentas(
    val id: String = "0",
    val nombre: String,
    val telefono: String,
    val deudaAcumulada: Double = 0.0
)