package ucne.edu.rocash.presentation.hojaRuta.crear

import ucne.edu.rocash.domain.estacion.model.EstacionVentas

/**
 * Snapshot inerte del armado de una hoja de ruta.
 *
 * Antes este UiState hacía cuatro cosas por su cuenta:
 *
 *   val cantidadSeleccionada: Int get() = estacionesSeleccionadas.size
 *   val puedeGuardar: Boolean get() = !isSaving && estacionesSeleccionadas.isNotEmpty()
 *   fun estaComprometida(estacionId: Int) = estacionId in estacionesComprometidas
 *   fun estaSeleccionada(estacionId: Int) = estacionId in estacionesSeleccionadas
 *
 * Las dos funciones eran el caso más costoso: la LazyColumn las llamaba dos
 * veces por fila y por recomposición, así que el estado dejaba de ser un dato
 * para convertirse en un servicio de consulta que la UI interrogaba en caliente.
 * Ahora la lista llega ya cruzada como [estaciones]: cada fila trae resueltas
 * sus propias banderas —el ViewModel hace ese cruce una vez por transición— y la
 * pantalla se limita a pintarlas.
 *
 * Los tres primeros campos siguen siendo la fuente de verdad cruda (lo que dice
 * el repositorio y lo que el usuario ha marcado); [estaciones] y los contadores
 * son su proyección ya resuelta, igual que `hayRutasAbiertas` en HomeUiState.
 *
 * [rutaCreadaId] y [errorMessage] siguen siendo banderas de una sola vez dentro
 * del estado, consumidas con `LaunchedEffect` y apagadas con su evento
 * correspondiente: es el patrón del Survival Guide y se mantiene por
 * consistencia con el resto del curso.
 */
data class CrearRutaUiState(
    val estacionesDisponibles: List<EstacionVentas> = emptyList(),
    val estacionesComprometidas: Set<Int> = emptySet(),
    val estacionesSeleccionadas: Set<Int> = emptySet(),

    val estaciones: List<EstacionSeleccionableUi> = emptyList(),
    val hayEstaciones: Boolean = false,
    val cantidadSeleccionada: Int = 0,
    val haySeleccion: Boolean = false,
    val puedeGuardar: Boolean = false,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    val rutaCreadaId: Int? = null,
    val errorMessage: String? = null
)

/** Una fila de la lista con su estado de selección ya resuelto. */
data class EstacionSeleccionableUi(
    val estacion: EstacionVentas,
    val seleccionada: Boolean,
    val comprometida: Boolean
)
