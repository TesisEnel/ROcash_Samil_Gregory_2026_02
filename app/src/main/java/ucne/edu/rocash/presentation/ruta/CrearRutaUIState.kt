package ucne.edu.rocash.presentation.ruta
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
data class CrearRutaUIState(
    val estacionesDisponibles: List<EstacionVentas> = emptyList(),
    val estacionesSeleccionadas: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)