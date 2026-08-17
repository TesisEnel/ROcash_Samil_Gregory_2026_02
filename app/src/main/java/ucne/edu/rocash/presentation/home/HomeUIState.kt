package ucne.edu.rocash.presentation.home

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

data class HomeUiState(
    val isLoading: Boolean = true,
    val rutasAbiertas: List<HojaRuta> = emptyList(),
    val totalIngresos: Double = 0.0,
    val rutasCompletadas: Int = 0,
    val sinSesion: Boolean = false,
    val errorMessage: String? = null
) {
    val hayRutasAbiertas: Boolean get() = rutasAbiertas.isNotEmpty()
}