package ucne.edu.rocash.domain.model

enum class EstadoVisita { COMPLETADA, OMITIDA }

data class RegistroRecoleccion(
    val id: String = "0",
    val hojaRutaId: Int,
    val estacionId: Int,
    val ventaBruta: Double,
    val porcentajeCliente: Double,
    val montoRecolectado: Double,
    val montoEsperado: Double = ventaBruta - porcentajeCliente,
    val montoDeuda: Double = montoEsperado - montoRecolectado,
    val estadoVisita: EstadoVisita,
    val notaIncidencia: String? = null
)