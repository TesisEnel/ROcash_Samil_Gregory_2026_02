package ucne.edu.rocash.domain.agenteVentas.usecase

import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject

class DeleteAgenteUseCase @Inject constructor(
    private val repository: AgenteVentasRepository
) {
    suspend operator fun invoke(id: Int) = repository.delete(id)
}