package ucne.edu.rocash.domain.agenteVentas.model

data class AgenteVentas(
    val id: String,
    val nombre: String,
    val telefono: String,
    val deudaAcumulada: Double = 0.0,
    val estado: Boolean = true
)