package ucne.edu.rocash.domain.model

enum class EstadoRuta { PENDIENTE, EN_PROGRESO, CERRADA }

data class HojaRuta(
    val id: String = "0",
    val recolectorId: Int,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val estado: EstadoRuta,
    val totalVentaBruta: Double = 0.0,
    val totalPorcentajeClientes: Double = 0.0,
    val totalRecaudado: Double = 0.0,
    val totalDeudas: Double = 0.0
)