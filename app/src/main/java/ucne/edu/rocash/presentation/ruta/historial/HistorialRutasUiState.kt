package ucne.edu.rocash.presentation.ruta.historial

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

data class HistorialRutasUiState(
    val isLoading: Boolean = true,
    val rutas: List<HojaRuta> = emptyList(),
    val errorMessage: String? = null
)