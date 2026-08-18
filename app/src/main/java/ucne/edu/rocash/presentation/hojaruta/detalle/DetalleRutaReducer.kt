package ucne.edu.rocash.presentation.hojaRuta.detalle

import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta

/**
 * Reducer puro del detalle de ruta.
 *
 * Toda derivación pasa por [recalcularDerivados], así que no existe forma de
 * dejar el estado a medio actualizar: cualquier transición que toque `ruta` o
 * `isCerrando` vuelve a resolver las banderas de una sola vez.
 */
object DetalleRutaReducer {

    fun cargando(estado: DetalleRutaUiState, rutaId: Int): DetalleRutaUiState =
        estado.copy(rutaId = rutaId, isLoading = true)

    fun conRuta(
        estado: DetalleRutaUiState,
        ruta: HojaRuta?,
        resumen: ResumenRecoleccionRuta
    ): DetalleRutaUiState = estado.copy(
        isLoading = false,
        ruta = ruta,
        resumen = resumen,
        noEncontrada = ruta == null
    ).recalcularDerivados()

    fun pidiendoConfirmacion(estado: DetalleRutaUiState): DetalleRutaUiState =
        estado.copy(mostrarDialogoCierre = true)

    fun cancelandoConfirmacion(estado: DetalleRutaUiState): DetalleRutaUiState =
        estado.copy(mostrarDialogoCierre = false)

    fun iniciandoCierre(estado: DetalleRutaUiState): DetalleRutaUiState =
        estado.copy(isCerrando = true, mostrarDialogoCierre = false).recalcularDerivados()

    fun cierreExitoso(estado: DetalleRutaUiState): DetalleRutaUiState =
        estado.copy(isCerrando = false, cierreCompletado = true).recalcularDerivados()

    fun cierreFallido(estado: DetalleRutaUiState, mensaje: String): DetalleRutaUiState =
        estado.copy(isCerrando = false, errorMessage = mensaje).recalcularDerivados()

    fun conFalloDeCarga(estado: DetalleRutaUiState, mensaje: String): DetalleRutaUiState =
        estado.copy(isLoading = false, errorMessage = mensaje)

    fun conMensaje(estado: DetalleRutaUiState, mensaje: String): DetalleRutaUiState =
        estado.copy(errorMessage = mensaje)

    fun sinMensaje(estado: DetalleRutaUiState): DetalleRutaUiState =
        estado.copy(errorMessage = null)

    /**
     * Único lugar donde se combinan la regla de dominio (`HojaRuta.puedeCerrarse`,
     * `HojaRuta.estacionesPendientes`) con el estado de presentación (`isCerrando`).
     */
    private fun DetalleRutaUiState.recalcularDerivados(): DetalleRutaUiState {
        val pendientes = ruta?.estacionesPendientes ?: 0
        val estaCerrada = ruta?.estado == EstadoRuta.CERRADA

        return copy(
            estacionesPendientes = pendientes,
            hayEstacionesPendientes = pendientes > 0,
            rutaEstaCerrada = estaCerrada,
            mostrarAccionCierre = ruta != null && !estaCerrada,
            puedeCerrarse = ruta?.puedeCerrarse == true && !isCerrando
        )
    }
}
