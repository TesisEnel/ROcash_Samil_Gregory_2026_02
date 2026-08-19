package ucne.edu.rocash.domain.abonoDeuda.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.abonoDeuda.repository.AbonoDeudaRepository
import javax.inject.Inject

class GetTotalAbonosGlobalUseCase @Inject constructor(
    private val repository: AbonoDeudaRepository
) {
    operator fun invoke(): Flow<Double> = repository.observarTotalAbonosGlobal()
}
