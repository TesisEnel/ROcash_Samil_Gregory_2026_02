package ucne.edu.rocash.presentation.estacion.detalle

import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

data class EstacionDetalleUiState(
    val isLoading: Boolean = true,
    val estacion: EstacionVentas? = null,
    val agente: AgenteVentas? = null,
    val historialAbonos: List<AbonoDeuda> = emptyList(),
    val errorMessage: String? = null
)