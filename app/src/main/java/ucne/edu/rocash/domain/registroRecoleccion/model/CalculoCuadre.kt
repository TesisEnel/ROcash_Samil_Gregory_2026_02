package ucne.edu.rocash.domain.registroRecoleccion.model

/**
 * Resultado del cálculo de un cuadre de banca.
 *
 * Antes esta fórmula estaba escrita tres veces: en los parámetros por defecto
 * de [RegistroRecoleccion], dentro de ProcesarRecoleccionUseCase y otra vez en
 * CuadreViewModel para el preview en pantalla. Tres copias de la misma regla de
 * dinero es la forma más barata de que la UI y la base de datos dejen de
 * coincidir. Ahora vive aquí y sólo aquí.
 */
data class CalculoCuadre(
    val ventaBruta: Double,
    val comisionCliente: Double,
    val montoRecolectado: Double,
    val montoEsperado: Double,
    val montoDeuda: Double
) {
    companion object {

        /** Cálculo puro y determinista: sin IO, sin corrutinas, sin Android. */
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

        /**
         * Variante tolerante para el preview en vivo del formulario: los campos
         * llegan como texto y pueden estar incompletos mientras el usuario
         * escribe. Un texto no numérico se trata como 0.0 y NO como error; la
         * validación real es responsabilidad de RecoleccionValidations.
         */
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
