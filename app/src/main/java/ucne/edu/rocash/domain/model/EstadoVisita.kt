package ucne.edu.rocash.domain.model

enum class EstadoVisita { COMPLETADA, OMITIDA }

data class RegistroRecoleccion(
    val id: String,
    val hojaRutaId: String,
    val estacionId: String,
    val ventaBruta: Double,
    val porcentajeCliente: Double,
    val montoRecolectado: Double,
    val montoEsperado: Double = ventaBruta - porcentajeCliente,
    val montoDeuda: Double = montoEsperado - montoRecolectado,
    val estadoVisita: EstadoVisita,
    val notaIncidencia: String? = null
)