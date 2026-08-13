package ucne.edu.rocash.domain.registroRecoleccion.model

enum class EstadoVisita { COMPLETADA, OMITIDA }

data class RegistroRecoleccion(
    val recoleccionId: Int = 0,
    val hojaRutaId: Int,
    val estacionId: Int,
    val ventaBruta: Double,
    val comisionCliente: Double,
    val montoEsperado: Double = ventaBruta - comisionCliente,
    val montoRecolectado: Double,
    val montoDeuda: Double = if (montoRecolectado < montoEsperado) montoEsperado - montoRecolectado else 0.0,
    val estadoVisita: EstadoVisita,
    val notaIncidencia: String? = null
)