package ucne.edu.rocash.domain.estacion.usecase

import ucne.edu.rocash.domain.estacion.repository.EstacionRepository
import javax.inject.Inject


class DeleteEstacionUseCase @Inject constructor(
    private val repository: EstacionRepository
) {
    suspend operator fun invoke(id: Int) = repository.delete(id)
}