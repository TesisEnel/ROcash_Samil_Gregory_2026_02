package ucne.edu.rocash.domain.estacion.usecase

import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.repository.EstacionRepository
import javax.inject.Inject

class UpsertEstacionUseCase @Inject constructor(
    private val repository: EstacionRepository
) {
    suspend operator fun invoke(estacion: EstacionVentas): Result<Int> {
        val nombreResult = validateEstacionNombre(estacion.nombre)
        if (!nombreResult.isValid) return Result.failure(IllegalArgumentException(nombreResult.error))

        val direccionResult = validateEstacionDireccion(estacion.direccion)
        if (!direccionResult.isValid) return Result.failure(IllegalArgumentException(direccionResult.error))

        val agenteResult = validateAgenteAsignado(estacion.agenteId)
        if (!agenteResult.isValid) return Result.failure(IllegalArgumentException(agenteResult.error))

        return runCatching { repository.upsert(estacion) }
    }
}