package ucne.edu.rocash.presentation.hojaRuta.historial

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

data class HistorialRutasUiState(
    val isLoading: Boolean = true,
    val rutas: List<HojaRuta> = emptyList(),
    val sinSesion: Boolean = false,
    val errorMessage: String? = null
) {
    val totalRecaudadoHistorico: Double get() = rutas.sumOf { it.totalRecaudado }
}