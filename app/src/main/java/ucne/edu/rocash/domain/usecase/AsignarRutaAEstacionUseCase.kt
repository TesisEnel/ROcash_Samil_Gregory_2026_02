package ucne.edu.rocash.domain.usecase

import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

class AsignarRutaAEstacionUseCase @Inject constructor(
    private val repository: RoCashRepository
) {
    suspend operator fun invoke(estacionId: String, rutaId: String) {
        repository.asignarRutaAEstacion(estacionId, rutaId)
    }
}