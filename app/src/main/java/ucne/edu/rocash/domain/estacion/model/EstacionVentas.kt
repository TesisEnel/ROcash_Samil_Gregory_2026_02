package ucne.edu.rocash.domain.estacion.model

data class EstacionVentas(
    val estacionId: Int = 0,
    val nombre: String,
    val direccion: String,
    val agenteId: Int,
    val agenteId2: Int? = null
)