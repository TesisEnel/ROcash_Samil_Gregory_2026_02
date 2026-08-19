package ucne.edu.rocash.domain.estacion.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.repository.EstacionRepository
import javax.inject.Inject

class SearchEstacionesUseCase @Inject constructor(
    private val repository: EstacionRepository
) {
    operator fun invoke(query: String): Flow<List<EstacionVentas>> = repository.buscarEstaciones(query)
}
