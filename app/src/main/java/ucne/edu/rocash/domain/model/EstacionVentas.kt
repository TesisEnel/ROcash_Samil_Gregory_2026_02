package ucne.edu.rocash.domain.model

data class EstacionVentas(
    val id: String,
    val hojaRutaId: String? = null,
    val nombre: String,
    val direccion: String,
    val agenteId: String
)
