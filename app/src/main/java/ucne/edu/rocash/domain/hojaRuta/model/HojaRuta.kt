package ucne.edu.rocash.domain.hojaRuta.model

import ucne.edu.rocash.domain.estacion.model.EstacionVentas

enum class EstadoRuta { PENDIENTE, EN_PROGRESO, CERRADA }

data class HojaRuta(
    val id: Int = 0,
    val recolectorId: String,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val estado: EstadoRuta,
    val totalVentaBruta: Double = 0.0,
    val totalComisionClientes: Double = 0.0,
    val totalRecaudado: Double = 0.0,
    val totalDeudas: Double = 0.0,
    val estaciones: List<EstacionVentas> = emptyList()
)