package ucne.edu.rocash.presentation.home

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

/**
 * Reducer puro del dashboard.
 *
 * Aquí vive lo que antes eran propiedades calculadas dentro de HomeUiState.
 * Al ser funciones puras y de nivel superior:
 *
 *  - el ViewModel las usa para reducir,
 *  - los tests las prueban sin corrutinas, sin Hilt y sin mocks,
 *  - los @Preview construyen estados coherentes con una sola llamada en vez de
 *    tener que acordarse de encender cada bandera a mano.
 */
object HomeReducer {

    fun conDatos(
        estado: HomeUiState,
        rutas: List<HojaRuta>,
        totalIngresos: Double,
        rutasCompletadas: Int
    ): HomeUiState = estado.copy(
        isLoading = false,
        sinSesion = false,
        rutasAbiertas = rutas,
        hayRutasAbiertas = rutas.isNotEmpty(),
        totalIngresos = totalIngresos,
        rutasCompletadas = rutasCompletadas,
        mostrarAccionNuevaRuta = true,
        errorMessage = null
    )

    fun sinSesion(estado: HomeUiState): HomeUiState = estado.copy(
        isLoading = false,
        sinSesion = true,
        mostrarAccionNuevaRuta = false
    )

    fun conFalloDeCarga(estado: HomeUiState, mensaje: String): HomeUiState = estado.copy(
        isLoading = false,
        errorMessage = mensaje
    )

    fun sinMensaje(estado: HomeUiState): HomeUiState = estado.copy(errorMessage = null)
}
