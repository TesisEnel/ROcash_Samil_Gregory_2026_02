package ucne.edu.rocash.presentation.home

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.presentation.core.UiState

/**
 * Snapshot inerte del dashboard.
 *
 * Antes tenía `val hayRutasAbiertas: Boolean get() = rutasAbiertas.isNotEmpty()`.
 * Ahora es un campo: el ViewModel lo resuelve al reducir y la pantalla sólo lo
 * lee.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val rutasAbiertas: List<HojaRuta> = emptyList(),
    val hayRutasAbiertas: Boolean = false,
    val totalIngresos: Double = 0.0,
    val rutasCompletadas: Int = 0,
    val sinSesion: Boolean = false,
    val mostrarAccionNuevaRuta: Boolean = false,
    val errorMessage: String? = null
) : UiState
