package ucne.edu.rocash.domain.estacion.usecase

import ucne.edu.rocash.domain.estacion.repository.EstacionRepository
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

class AsignarRutaAEstacionUseCase @Inject constructor(
    private val repository: EstacionRepository
) {
    suspend operator fun invoke(estacionId: String, rutaId: Int) {
        repository.asignarRuta(estacionId, rutaId)
    }
}