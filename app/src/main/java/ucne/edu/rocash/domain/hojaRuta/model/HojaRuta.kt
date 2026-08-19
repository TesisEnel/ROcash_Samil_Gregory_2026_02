package ucne.edu.rocash.domain.hojaRuta.model
enum class EstadoRuta {
    PENDIENTE,
    EN_PROGRESO,
    CERRADA;

    val estaAbierta: Boolean
        get() = this == PENDIENTE || this == EN_PROGRESO

    companion object {
        val ABIERTOS: List<EstadoRuta> = listOf(PENDIENTE, EN_PROGRESO)
        val NOMBRES_ABIERTOS: List<String> = ABIERTOS.map { it.name }
    }
}

data class HojaRuta(
    val id: Int = 0,
    val recolectorId: String,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaCierre: Long? = null,
    val estado: EstadoRuta = EstadoRuta.PENDIENTE,
    val totalVentaBruta: Double = 0.0,
    val totalComisionClientes: Double = 0.0,
    val totalRecaudado: Double = 0.0,
    val totalDeudas: Double = 0.0,
    val estaciones: List<EstacionEnRuta> = emptyList()
) {
    val cantidadEstaciones: Int get() = estaciones.size

    val estacionesCuadradas: Int
        get() = estaciones.count { it.estado != EstadoVisitaEstacion.PENDIENTE }

    val estacionesPendientes: Int
        get() = estaciones.count { it.estado == EstadoVisitaEstacion.PENDIENTE }

    val porcentajeAvance: Float
        get() = if (cantidadEstaciones == 0) 0f
        else estacionesCuadradas.toFloat() / cantidadEstaciones

    val puedeCerrarse: Boolean
        get() = estado.estaAbierta &&
                estaciones.isNotEmpty() &&
                estacionesPendientes == 0
}
