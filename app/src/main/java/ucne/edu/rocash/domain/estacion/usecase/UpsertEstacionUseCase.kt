package ucne.edu.rocash.domain.estacion.usecase

import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.repository.EstacionRepository
import javax.inject.Inject

class UpsertEstacionUseCase @Inject constructor(
    private val repository: EstacionRepository,
    private val agenteRepository: AgenteVentasRepository
) {
    suspend operator fun invoke(estacion: EstacionVentas): Result<Int> {
        val nombreResult = validateEstacionNombre(estacion.nombre)
        if (!nombreResult.isValid) {
            return Result.failure(IllegalArgumentException(nombreResult.error))
        }

        val direccionResult = validateEstacionDireccion(estacion.direccion)
        if (!direccionResult.isValid) {
            return Result.failure(IllegalArgumentException(direccionResult.error))
        }

        val agenteResult = validateAgenteAsignado(estacion.agenteId)
        if (!agenteResult.isValid) {
            return Result.failure(IllegalArgumentException(agenteResult.error))
        }

        val distintosResult = validateAgentesDistintos(estacion.agenteId, estacion.agenteId2)
        if (!distintosResult.isValid) {
            return Result.failure(IllegalArgumentException(distintosResult.error))
        }

        return runCatching {
            // Un agente pertenece a una sola banca. La deuda se acumula por
            // agente, así que tenerlo en dos bancas mezclaría faltantes de
            // ambas en un mismo saldo.
            verificarAgenteLibre(estacion.agenteId, estacion.estacionId)
            estacion.agenteId2?.let { verificarAgenteLibre(it, estacion.estacionId) }

            repository.upsert(estacion)
        }
    }

    private suspend fun verificarAgenteLibre(agenteId: Int, estacionId: Int) {
        val ocupadas = repository.bancasDelAgente(
            agenteId = agenteId,
            estacionIdExcluida = estacionId
        )

        val validacion = validateAgenteLibre(
            nombreAgente = agenteRepository.getAgente(agenteId)?.nombre ?: "El agente",
            bancasDondeYaFigura = ocupadas
        )

        if (!validacion.isValid) {
            throw IllegalStateException(validacion.error)
        }
    }
}
