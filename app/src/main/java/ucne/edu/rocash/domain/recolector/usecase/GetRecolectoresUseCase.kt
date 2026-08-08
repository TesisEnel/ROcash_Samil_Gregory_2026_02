package ucne.edu.rocash.domain.recolector.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.recolector.model.Recolector
import ucne.edu.rocash.domain.recolector.repository.RecolectorRepository
import javax.inject.Inject

class GetRecolectoresUseCase @Inject constructor(
    private val repository: RecolectorRepository
) {
    operator fun invoke(): Flow<List<Recolector>> {
        return repository.obtenerRecolectores()
    }
}