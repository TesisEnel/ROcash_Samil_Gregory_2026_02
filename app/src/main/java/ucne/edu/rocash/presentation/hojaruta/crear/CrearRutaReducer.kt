package ucne.edu.rocash.presentation.hojaRuta.crear

import ucne.edu.rocash.domain.estacion.model.EstacionVentas

/**
 * Reducer puro del armado de rutas.
 *
 * Aquí viven las cuatro derivaciones que antes estaban dentro de
 * CrearRutaUiState. Toda transición pasa por [recalcularDerivados], de modo que
 * la lista proyectada y los contadores no pueden quedar desfasados respecto a
 * los conjuntos crudos.
 */
object CrearRutaReducer {

    fun conEstaciones(
        estado: CrearRutaUiState,
        disponibles: List<EstacionVentas>,
        comprometidas: Set<Int>
    ): CrearRutaUiState = estado.copy(
        isLoading = false,
        estacionesDisponibles = disponibles,
        estacionesComprometidas = comprometidas,
        // Una estación que pasó a estar comprometida deja de estar seleccionable.
        estacionesSeleccionadas = estado.estacionesSeleccionadas - comprometidas,
        errorMessage = null
    ).recalcularDerivados()

    fun conEstacionAlternada(
        estado: CrearRutaUiState,
        estacionId: Int
    ): CrearRutaUiState {
        if (estacionId in estado.estacionesComprometidas) return estado

        val seleccionadas =
            if (estacionId in estado.estacionesSeleccionadas) {
                estado.estacionesSeleccionadas - estacionId
            } else {
                estado.estacionesSeleccionadas + estacionId
            }

        return estado.copy(
            estacionesSeleccionadas = seleccionadas,
            errorMessage = null
        ).recalcularDerivados()
    }

    fun sinSeleccion(estado: CrearRutaUiState): CrearRutaUiState =
        estado.copy(estacionesSeleccionadas = emptySet()).recalcularDerivados()

    fun guardando(estado: CrearRutaUiState): CrearRutaUiState =
        estado.copy(isSaving = true, errorMessage = null).recalcularDerivados()

    fun rutaCreada(estado: CrearRutaUiState, rutaId: Int): CrearRutaUiState =
        estado.copy(isSaving = false, rutaCreadaId = rutaId).recalcularDerivados()

    fun guardadoFallido(estado: CrearRutaUiState, mensaje: String): CrearRutaUiState =
        estado.copy(isSaving = false, errorMessage = mensaje).recalcularDerivados()

    fun conFalloDeCarga(estado: CrearRutaUiState, mensaje: String): CrearRutaUiState =
        estado.copy(isLoading = false, errorMessage = mensaje).recalcularDerivados()

    fun sinMensaje(estado: CrearRutaUiState): CrearRutaUiState =
        estado.copy(errorMessage = null)

    /**
     * Único lugar donde se cruzan estaciones, comprometidas y seleccionadas.
     *
     * El cruce se hace una vez por transición en lugar de una vez por fila y por
     * recomposición, que es lo que ocurría cuando la pantalla llamaba a
     * `state.estaSeleccionada(...)` dentro del `items { }`.
     */
    private fun CrearRutaUiState.recalcularDerivados(): CrearRutaUiState {
        val filas = estacionesDisponibles.map { estacion ->
            EstacionSeleccionableUi(
                estacion = estacion,
                seleccionada = estacion.estacionId in estacionesSeleccionadas,
                comprometida = estacion.estacionId in estacionesComprometidas
            )
        }

        return copy(
            estaciones = filas,
            hayEstaciones = filas.isNotEmpty(),
            cantidadSeleccionada = estacionesSeleccionadas.size,
            haySeleccion = estacionesSeleccionadas.isNotEmpty(),
            puedeGuardar = !isSaving && estacionesSeleccionadas.isNotEmpty()
        )
    }
}
