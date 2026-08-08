package ucne.edu.rocash.domain.estacion.usecase

import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.repository.EstacionRepository
import javax.inject.Inject

class SaveEstacionUseCase @Inject constructor(
    private val repository: EstacionRepository
) {
    suspend operator fun invoke(estacion: EstacionVentas) = repository.insertarEstacion(estacion)
}