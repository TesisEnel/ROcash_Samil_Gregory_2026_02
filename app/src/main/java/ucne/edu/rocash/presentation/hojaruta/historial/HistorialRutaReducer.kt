package ucne.edu.rocash.presentation.hojaRuta.historial

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

/** Reducer puro del historial. */
object HistorialRutasReducer {

    fun conHistorial(
        estado: HistorialRutasUiState,
        rutas: List<HojaRuta>,
        totalRecaudadoHistorico: Double
    ): HistorialRutasUiState = estado.copy(
        isLoading = false,
        sinSesion = false,
        rutas = rutas,
        cantidadRutas = rutas.size,
        hayRutas = rutas.isNotEmpty(),
        totalRecaudadoHistorico = totalRecaudadoHistorico,
        errorMessage = null
    )

    fun sinSesion(estado: HistorialRutasUiState): HistorialRutasUiState = estado.copy(
        isLoading = false,
        sinSesion = true
    )

    fun conFalloDeCarga(
        estado: HistorialRutasUiState,
        mensaje: String
    ): HistorialRutasUiState = estado.copy(isLoading = false, errorMessage = mensaje)

    fun sinMensaje(estado: HistorialRutasUiState): HistorialRutasUiState =
        estado.copy(errorMessage = null)
}
