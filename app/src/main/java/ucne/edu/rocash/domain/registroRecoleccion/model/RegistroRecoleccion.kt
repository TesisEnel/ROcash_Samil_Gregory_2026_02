package ucne.edu.rocash.domain.registroRecoleccion.model

enum class EstadoVisita { COMPLETADA, OMITIDA }

/**
 * Cuadre de una banca dentro de una hoja de ruta.
 *
 * [montoEsperado] y [montoDeuda] ya NO se calculan en los parámetros por
 * defecto. Un default que hace aritmética de dinero se ejecuta o no según qué
 * argumentos pase cada llamador, así que el mismo `data class` podía terminar
 * con cifras coherentes o incoherentes sin que nada lo delatara. Ahora el
 * modelo sólo transporta datos ya calculados y quien los produce es
 * [CalculoCuadre].
 */
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

        /** Construye un registro derivando los montos desde [CalculoCuadre]. */
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
