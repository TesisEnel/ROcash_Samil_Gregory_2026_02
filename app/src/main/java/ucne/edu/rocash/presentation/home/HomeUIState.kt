package ucne.edu.rocash.presentation.home

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

data class HomeUiState(
    val isLoading: Boolean = true,
    val rutasAbiertas: List<HojaRuta> = emptyList(),
    val hayRutasAbiertas: Boolean = false,
    val totalIngresos: Double = 0.0,
    val rutasCompletadas: Int = 0,
    val sinSesion: Boolean = false,
    val mostrarAccionNuevaRuta: Boolean = false,
    val errorMessage: String? = null
)
