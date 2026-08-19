package ucne.edu.rocash.presentation.hojaRuta.crear

import ucne.edu.rocash.domain.estacion.model.EstacionVentas
data class CrearRutaUiState(
    val estacionesDisponibles: List<EstacionVentas> = emptyList(),
    val estacionesComprometidas: Set<Int> = emptySet(),
    val estacionesSeleccionadas: Set<Int> = emptySet(),

    val estaciones: List<EstacionSeleccionableUi> = emptyList(),
    val hayEstaciones: Boolean = false,
    val cantidadSeleccionada: Int = 0,
    val haySeleccion: Boolean = false,
    val puedeGuardar: Boolean = false,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    val rutaCreadaId: Int? = null,
    val errorMessage: String? = null
)

data class EstacionSeleccionableUi(
    val estacion: EstacionVentas,
    val seleccionada: Boolean,
    val comprometida: Boolean
)
