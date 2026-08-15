package ucne.edu.rocash.data.registroRecoleccion.local

data class ResumenRecoleccionRutaEntity(
    val totalVentaBruta: Double,
    val totalComisionClientes: Double,
    val totalRecaudado: Double,
    val totalDeudas: Double,
    val cantidadRegistros: Int
)