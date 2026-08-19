package ucne.edu.rocash.domain.registroRecoleccion.model

data class CalculoCuadre(
    val ventaBruta: Double,
    val comisionCliente: Double,
    val montoRecolectado: Double,
    val montoEsperado: Double,
    val montoDeuda: Double
) {
    companion object {
        fun desde(
            ventaBruta: Double,
            comisionCliente: Double,
            montoRecolectado: Double
        ): CalculoCuadre {
            val esperado = ventaBruta - comisionCliente
            val deuda = (esperado - montoRecolectado).coerceAtLeast(0.0)

            return CalculoCuadre(
                ventaBruta = ventaBruta,
                comisionCliente = comisionCliente,
                montoRecolectado = montoRecolectado,
                montoEsperado = esperado,
                montoDeuda = deuda
            )
        }

        fun desdeTexto(
            ventaBruta: String,
            comisionCliente: String,
            montoRecolectado: String
        ): CalculoCuadre = desde(
            ventaBruta = ventaBruta.toDoubleOrNull() ?: 0.0,
            comisionCliente = comisionCliente.toDoubleOrNull() ?: 0.0,
            montoRecolectado = montoRecolectado.toDoubleOrNull() ?: 0.0
        )
    }
}
