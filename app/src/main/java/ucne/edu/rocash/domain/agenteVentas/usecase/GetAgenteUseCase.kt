package ucne.edu.rocash.domain.agenteVentas.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject

class GetAgenteUseCase @Inject constructor(
    private val repository: AgenteVentasRepository
) {
    suspend operator fun invoke(id: Int): AgenteVentas? = repository.getAgente(id)
}
