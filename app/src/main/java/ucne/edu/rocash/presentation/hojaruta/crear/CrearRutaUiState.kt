package ucne.edu.rocash.presentation.hojaRuta.crear

import ucne.edu.rocash.domain.estacion.model.EstacionVentas

data class CrearRutaUiState(
    val estacionesDisponibles: List<EstacionVentas> = emptyList(),
    val estacionesComprometidas: Set<Int> = emptySet(),
    val estacionesSeleccionadas: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val rutaCreadaId: Int? = null,
    val errorMessage: String? = null
) {
    val cantidadSeleccionada: Int get() = estacionesSeleccionadas.size

    val puedeGuardar: Boolean get() = !isSaving && estacionesSeleccionadas.isNotEmpty()

    fun estaComprometida(estacionId: Int): Boolean = estacionId in estacionesComprometidas

    fun estaSeleccionada(estacionId: Int): Boolean = estacionId in estacionesSeleccionadas
}