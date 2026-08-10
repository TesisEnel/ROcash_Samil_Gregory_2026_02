package ucne.edu.rocash.presentation.home


import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta


data class HomeUIState(
    val isLoading: Boolean = true,
    val hojaRutaActiva: HojaRuta? = null,
    val totalIngresos: Double = 0.0,
    val rutasCompletadas: Int = 0,
    val errorMessage: String? = null
)