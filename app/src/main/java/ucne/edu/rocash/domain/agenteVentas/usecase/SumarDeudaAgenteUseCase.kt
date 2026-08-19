package ucne.edu.rocash.domain.agenteVentas.usecase

import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject

class SumarDeudaAgenteUseCase @Inject constructor(
    private val repository: AgenteVentasRepository
) {
    suspend operator fun invoke(agenteId: Int, monto: Double) {
        if (monto == 0.0) return
        repository.sumarDeuda(agenteId, monto)
    }
}
