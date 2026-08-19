package ucne.edu.rocash.domain.registroRecoleccion.model

enum class EstadoVisita { COMPLETADA, OMITIDA }


data class RegistroRecoleccion(
    val recoleccionId: Int = 0,
    val hojaRutaId: Int,
    val estacionId: Int,
    val ventaBruta: Double,
    val comisionCliente: Double,
    val montoEsperado: Double,
    val montoRecolectado: Double,
    val montoDeuda: Double,
    val estadoVisita: EstadoVisita = EstadoVisita.COMPLETADA,
    val notaIncidencia: String? = null
) {
    companion object {

        fun desdeCalculo(
            recoleccionId: Int = 0,
            hojaRutaId: Int,
            estacionId: Int,
            calculo: CalculoCuadre,
            estadoVisita: EstadoVisita = EstadoVisita.COMPLETADA,
            notaIncidencia: String? = null
        ): RegistroRecoleccion = RegistroRecoleccion(
            recoleccionId = recoleccionId,
            hojaRutaId = hojaRutaId,
            estacionId = estacionId,
            ventaBruta = calculo.ventaBruta,
            comisionCliente = calculo.comisionCliente,
            montoEsperado = calculo.montoEsperado,
            montoRecolectado = calculo.montoRecolectado,
            montoDeuda = calculo.montoDeuda,
            estadoVisita = estadoVisita,
            notaIncidencia = notaIncidencia
        )
    }
}
