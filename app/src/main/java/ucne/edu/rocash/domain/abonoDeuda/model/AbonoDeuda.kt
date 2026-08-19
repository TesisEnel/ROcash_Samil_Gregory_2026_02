package ucne.edu.rocash.domain.abonoDeuda.model

data class AbonoDeuda(
    val abonoId: Int = 0,
    val agenteId: Int,
    val monto: Double,
    val deudaAntes: Double,
    val deudaDespues: Double,
    val fecha: Long = System.currentTimeMillis(),
    val nota: String? = null
)
