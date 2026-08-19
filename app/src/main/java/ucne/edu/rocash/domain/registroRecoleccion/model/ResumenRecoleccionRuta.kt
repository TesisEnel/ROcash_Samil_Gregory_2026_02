package ucne.edu.rocash.domain.registroRecoleccion.model
data class ResumenRecoleccionRuta(
    val totalVentaBruta: Double = 0.0,
    val totalComisionClientes: Double = 0.0,
    val totalRecaudado: Double = 0.0,
    val totalDeudas: Double = 0.0,
    val cantidadRegistros: Int = 0
)
