package ucne.edu.rocash.presentation.ruta
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
data class CrearRutaUIState(
    val isLoading: Boolean = true,
    val estacionesDisponibles: List<EstacionVentas> = emptyList(),
    val estacionesSeleccionadas: Set<String> = emptySet(),
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)