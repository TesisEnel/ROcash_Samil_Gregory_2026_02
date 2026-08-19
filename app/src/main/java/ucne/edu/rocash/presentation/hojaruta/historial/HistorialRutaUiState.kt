package ucne.edu.rocash.presentation.hojaRuta.historial

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

data class HistorialRutasUiState(
    val isLoading: Boolean = true,
    val rutas: List<HojaRuta> = emptyList(),
    val cantidadRutas: Int = 0,
    val totalRecaudadoHistorico: Double = 0.0,
    val hayRutas: Boolean = false,
    val sinSesion: Boolean = false,
    val errorMessage: String? = null
)
