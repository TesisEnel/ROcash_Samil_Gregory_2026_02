package ucne.edu.rocash.presentation.home

import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.model.HojaRuta

data class HomeUIState(
    val isLoading: Boolean = true,
    val hojaRutaActiva: HojaRuta? = null,
    val estaciones: List<EstacionVentas> = emptyList(),
    val errorMessage: String? = null
)