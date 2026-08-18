package ucne.edu.rocash.presentation.hojaRuta.cuadre

import ucne.edu.rocash.domain.registroRecoleccion.model.CalculoCuadre
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion

/** Cuál de los tres campos de dinero acaba de tocar el usuario. */
enum class CampoMonto { VENTA_BRUTA, COMISION, MONTO_RECOLECTADO }

/**
 * Reducer puro del formulario de cuadre.
 *
 * Aquí vive lo que antes era `puedeGuardar` dentro de CuadreUiState. Nótese que
 * el reducer NO calcula dinero: recibe un [CalculoCuadre] ya resuelto por el
 * dominio y sólo deriva decisiones de presentación (si el botón se habilita, si
 * hay que pintar la deuda, si esa deuda se reparte entre dos agentes).
 *
 * Toda transición que toque montos o banderas de carga pasa por
 * [recalcularDerivados], así que no existe forma de dejar el estado a medias.
 */
object CuadreReducer {

    fun cargando(
        estado: CuadreUiState,
        hojaRutaId: Int,
        estacionId: Int,
        agenteId: Int,
        nombreEstacion: String
    ): CuadreUiState = estado.copy(
        hojaRutaId = hojaRutaId,
        estacionId = estacionId,
        agenteId = agenteId,
        nombreEstacion = nombreEstacion,
        isLoading = true
    ).recalcularDerivados()

    /** No había cuadre registrado para esta banca en esta ruta. */
    fun comoCuadreNuevo(estado: CuadreUiState, agenteId2: Int?): CuadreUiState = estado.copy(
        isLoading = false,
        isNew = true,
        agenteId2 = agenteId2
    ).recalcularDerivados()

    /** Ya existía un cuadre: el formulario se abre en modo edición con sus montos. */
    fun conCuadrePrevio(
        estado: CuadreUiState,
        previo: RegistroRecoleccion,
        agenteId2: Int?
    ): CuadreUiState = estado.copy(
        isLoading = false,
        isNew = false,
        agenteId2 = agenteId2,
        ventaBruta = previo.ventaBruta.toString(),
        comisionCliente = previo.comisionCliente.toString(),
        montoRecolectado = previo.montoRecolectado.toString(),
        notaIncidencia = previo.notaIncidencia.orEmpty(),
        montoEsperado = previo.montoEsperado,
        deudaGenerada = previo.montoDeuda
    ).recalcularDerivados()

    /**
     * Deposita los tres textos y el [calculo] que el dominio produjo con ellos.
     *
     * [campoEditado] indica qué campo tocó el usuario para limpiar únicamente su
     * error de validación; el resto se conserva. Con `null` no se limpia ninguno
     * (lo usan los @Preview y los tests, que no vienen de una edición real).
     */
    fun conMontos(
        estado: CuadreUiState,
        campoEditado: CampoMonto?,
        ventaBruta: String,
        comisionCliente: String,
        montoRecolectado: String,
        calculo: CalculoCuadre
    ): CuadreUiState = estado.copy(
        ventaBruta = ventaBruta,
        comisionCliente = comisionCliente,
        montoRecolectado = montoRecolectado,
        ventaBrutaError =
            if (campoEditado == CampoMonto.VENTA_BRUTA) null else estado.ventaBrutaError,
        comisionError =
            if (campoEditado == CampoMonto.COMISION) null else estado.comisionError,
        montoRecolectadoError =
            if (campoEditado == CampoMonto.MONTO_RECOLECTADO) null
            else estado.montoRecolectadoError,
        montoEsperado = calculo.montoEsperado,
        deudaGenerada = calculo.montoDeuda
    ).recalcularDerivados()

    /** Atajo para @Preview y tests: estado coherente a partir de los tres textos. */
    fun conMontosDeTexto(
        estado: CuadreUiState,
        ventaBruta: String,
        comisionCliente: String,
        montoRecolectado: String
    ): CuadreUiState = conMontos(
        estado = estado,
        campoEditado = null,
        ventaBruta = ventaBruta,
        comisionCliente = comisionCliente,
        montoRecolectado = montoRecolectado,
        calculo = CalculoCuadre.desdeTexto(
            ventaBruta = ventaBruta,
            comisionCliente = comisionCliente,
            montoRecolectado = montoRecolectado
        )
    )

    fun conNota(estado: CuadreUiState, nota: String): CuadreUiState =
        estado.copy(notaIncidencia = nota)

    fun conErroresDeValidacion(
        estado: CuadreUiState,
        ventaBrutaError: String?,
        comisionError: String?,
        montoRecolectadoError: String?
    ): CuadreUiState = estado.copy(
        ventaBrutaError = ventaBrutaError,
        comisionError = comisionError,
        montoRecolectadoError = montoRecolectadoError
    ).recalcularDerivados()

    fun guardando(estado: CuadreUiState): CuadreUiState =
        estado.copy(isSaving = true, errorMessage = null).recalcularDerivados()

    fun guardadoExitoso(estado: CuadreUiState): CuadreUiState =
        estado.copy(isSaving = false, saved = true).recalcularDerivados()

    fun guardadoFallido(estado: CuadreUiState, mensaje: String): CuadreUiState =
        estado.copy(isSaving = false, errorMessage = mensaje).recalcularDerivados()

    fun sinMensaje(estado: CuadreUiState): CuadreUiState =
        estado.copy(errorMessage = null)

    /**
     * Único lugar donde se decide qué se habilita y qué se pinta.
     *
     * `puedeGuardar` es exactamente la regla que antes vivía en el UiState; la
     * diferencia es que ahora se evalúa una vez por transición y no una vez por
     * recomposición.
     */
    private fun CuadreUiState.recalcularDerivados(): CuadreUiState = copy(
        hayDeuda = deudaGenerada > 0.0,
        deudaSeReparte = deudaGenerada > 0.0 && agenteId2 != null,
        puedeGuardar = !isSaving &&
                !isLoading &&
                ventaBruta.isNotBlank() &&
                comisionCliente.isNotBlank() &&
                montoRecolectado.isNotBlank()
    )
}
