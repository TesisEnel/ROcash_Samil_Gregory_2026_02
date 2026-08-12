package ucne.edu.rocash.domain.model

enum class EstadoVisita { COMPLETADA, OMITIDA }

data class RegistroRecoleccion(
    val id: String,
    val hojaRutaId: Int, // Cambiado a Int para coincidir con HojaRuta
    val estacionId: String,
    val ventaBruta: Double,
    val comisionCliente: Double,
    val montoEsperado: Double = ventaBruta - comisionCliente,
    val montoRecolectado: Double,
    val montoDeuda: Double = if (montoRecolectado < montoEsperado) montoEsperado - montoRecolectado else 0.0,
    val estadoVisita: EstadoVisita,
    val notaIncidencia: String? = null
)