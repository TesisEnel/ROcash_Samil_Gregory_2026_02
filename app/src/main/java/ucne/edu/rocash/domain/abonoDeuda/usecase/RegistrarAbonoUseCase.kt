package ucne.edu.rocash.domain.abonoDeuda.usecase

import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda
import ucne.edu.rocash.domain.abonoDeuda.repository.AbonoDeudaRepository
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject
class RegistrarAbonoUseCase @Inject constructor(
    private val abonoRepository: AbonoDeudaRepository,
    private val agenteRepository: AgenteVentasRepository
) {
    suspend operator fun invoke(
        agenteId: Int,
        montoTexto: String,
        nota: String? = null
    ): Result<AbonoDeuda> {
        val agente = agenteRepository.getAgente(agenteId)
            ?: return Result.failure(IllegalStateException("El agente no existe"))

        val validacion = validateMontoAbono(montoTexto, agente.deudaAcumulada)
        if (!validacion.isValid) {
            return Result.failure(IllegalArgumentException(validacion.error))
        }

        val monto = montoTexto.toDouble()

        return runCatching {
            agenteRepository.sumarDeuda(agenteId, -monto)

            val abono = AbonoDeuda(
                agenteId = agenteId,
                monto = monto,
                deudaAntes = agente.deudaAcumulada,
                deudaDespues = agente.deudaAcumulada - monto,
                nota = nota?.takeIf { it.isNotBlank() }
            )

            val id = abonoRepository.registrar(abono)
            abono.copy(abonoId = id)
        }
    }
}
