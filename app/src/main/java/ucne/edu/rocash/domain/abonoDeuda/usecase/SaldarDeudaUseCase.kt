package ucne.edu.rocash.domain.abonoDeuda.usecase

import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda
import ucne.edu.rocash.domain.abonoDeuda.repository.AbonoDeudaRepository
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject

/**
 * Deja la deuda del agente en cero y lo registra como un abono por el total.
 *
 * Saldar es un abono, no una operación aparte: si desapareciera del historial,
 * la deuda bajaría de golpe sin nada que lo explique.
 */
class SaldarDeudaUseCase @Inject constructor(
    private val abonoRepository: AbonoDeudaRepository,
    private val agenteRepository: AgenteVentasRepository
) {
    suspend operator fun invoke(agenteId: Int, nota: String? = null): Result<AbonoDeuda> {
        val agente = agenteRepository.getAgente(agenteId)
            ?: return Result.failure(IllegalStateException("El agente no existe"))

        if (agente.deudaAcumulada <= 0.0) {
            return Result.failure(IllegalStateException("Este agente no tiene deuda pendiente"))
        }

        return runCatching {
            val total = agente.deudaAcumulada
            agenteRepository.sumarDeuda(agenteId, -total)

            val abono = AbonoDeuda(
                agenteId = agenteId,
                monto = total,
                deudaAntes = total,
                deudaDespues = 0.0,
                nota = nota?.takeIf { it.isNotBlank() } ?: "Deuda saldada"
            )

            val id = abonoRepository.registrar(abono)
            abono.copy(abonoId = id)
        }
    }
}
