package ucne.edu.rocash.domain.recolector.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.recolector.model.Recolector
import ucne.edu.rocash.domain.recolector.repository.RecolectorRepository
import javax.inject.Inject

class SearchRecolectoresUseCase @Inject constructor(
    private val repository: RecolectorRepository
) {
    operator fun invoke(query: String): Flow<List<Recolector>> {
        return repository.buscarRecolectoresPorNombre(query)
    }
}