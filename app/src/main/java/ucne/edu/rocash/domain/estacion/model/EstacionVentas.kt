package ucne.edu.rocash.domain.estacion.model

data class EstacionVentas(
    val id: String,
    val hojaRutaId: Int? = null,
    val nombre: String,
    val direccion: String,
    val agenteId: String,
    val agenteId2: String? = null
)