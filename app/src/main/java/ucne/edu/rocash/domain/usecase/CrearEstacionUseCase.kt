package ucne.edu.rocash.domain.usecase

import ucne.edu.rocash.domain.model.EstacionVentas
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

class CrearEstacionUseCase @Inject constructor(
    private val repository: RoCashRepository
) {
    suspend operator fun invoke(estacion: EstacionVentas) {
        repository.insertarEstacion(estacion)
    }
}