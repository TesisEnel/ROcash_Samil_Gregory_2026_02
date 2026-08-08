package ucne.edu.rocash.domain.agenteVentas.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject

class SearchAgentesUseCase @Inject constructor(
    private val repository: AgenteVentasRepository
) {
    operator fun invoke(query: String): Flow<List<AgenteVentas>> = repository.buscarAgentesPorNombre(query)
}