package ucne.edu.rocash.domain.abonoDeuda.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda
import ucne.edu.rocash.domain.abonoDeuda.repository.AbonoDeudaRepository
import javax.inject.Inject

class ObservarAbonosDeAgenteUseCase @Inject constructor(
    private val repository: AbonoDeudaRepository
) {
    operator fun invoke(agenteId: Int): Flow<List<AbonoDeuda>> =
        repository.observarAbonosDeAgente(agenteId)
}
