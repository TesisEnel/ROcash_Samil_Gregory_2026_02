package ucne.edu.rocash.domain.abonoDeuda.model

/**
 * Un pago del agente contra su deuda acumulada.
 *
 * Existe como registro propio y no solo como una resta sobre
 * `AgenteVentas.deudaAcumulada` porque en un negocio de efectivo la pregunta
 * que siempre aparece es "¿cuándo y cuánto pagó?". Un saldo que baja sin dejar
 * rastro no se puede defender ante el agente ni ante el dueño de la agencia.
 */
data class AbonoDeuda(
    val abonoId: Int = 0,
    val agenteId: Int,
    val monto: Double,
    val deudaAntes: Double,
    val deudaDespues: Double,
    val fecha: Long = System.currentTimeMillis(),
    val nota: String? = null
)
