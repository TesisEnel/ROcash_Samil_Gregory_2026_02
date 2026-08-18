package ucne.edu.rocash.presentation.hojaRuta.historial

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.presentation.core.UiState

/**
 * Snapshot inerte del historial.
 *
 * `totalRecaudadoHistorico` era `get() = rutas.sumOf { it.totalRecaudado }`.
 * Ese `sumOf` era el caso más claro del problema: una acumulación de dinero
 * ejecutándose en la capa de UI, en cada recomposición, sobre la lista completa.
 * Peor aún, duplicaba `HojaRutaDao.observarTotalIngresos`, que ya calcula
 * exactamente lo mismo con `SUM(totalRecaudado)` en SQLite. Ahora el total llega
 * desde el dominio y aquí sólo se guarda.
 */
data class HistorialRutasUiState(
    val isLoading: Boolean = true,
    val rutas: List<HojaRuta> = emptyList(),
    val cantidadRutas: Int = 0,
    val totalRecaudadoHistorico: Double = 0.0,
    val hayRutas: Boolean = false,
    val sinSesion: Boolean = false,
    val errorMessage: String? = null
) : UiState
