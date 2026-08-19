package ucne.edu.rocash.domain.agenteVentas.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject
class ObservarAgenteUseCase @Inject constructor(
    private val repository: AgenteVentasRepository
) {
    operator fun invoke(agenteId: Int): Flow<AgenteVentas?> =
        repository.observeAgentes().map { lista -> lista.find { it.agenteId == agenteId } }
}
