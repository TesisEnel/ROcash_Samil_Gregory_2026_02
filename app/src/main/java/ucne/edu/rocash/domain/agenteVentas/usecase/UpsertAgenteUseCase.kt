package ucne.edu.rocash.domain.agenteVentas.usecase

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject

class UpsertAgenteUseCase @Inject constructor(
    private val repository: AgenteVentasRepository
) {
    suspend operator fun invoke(agente: AgenteVentas): Result<Int> {
        val nombreResult = validateNombre(agente.nombre)
        if (!nombreResult.isValid) {
            return Result.failure(IllegalArgumentException(nombreResult.error))
        }
        val telefonoResult = validateTelefono(agente.telefono)
        if (!telefonoResult.isValid) {
            return Result.failure(IllegalArgumentException(telefonoResult.error))
        }
        return runCatching { repository.upsert(agente) }
    }
}