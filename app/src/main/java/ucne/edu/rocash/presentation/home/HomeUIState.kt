package ucne.edu.rocash.presentation.home

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

data class HomeUiState(
    val isLoading: Boolean = true,
    val sinSesion: Boolean = false,
    val rutasAbiertas: List<HojaRuta> = emptyList(),
    val hayRutasAbiertas: Boolean = false,
    val totalIngresosRutas: Double = 0.0,
    val totalAbonos: Double = 0.0,
    val totalIngresos: Double = 0.0,
    val rutasCompletadas: Int = 0,
    val mostrarAccionNuevaRuta: Boolean = false,
    val errorMessage: String? = null
)
