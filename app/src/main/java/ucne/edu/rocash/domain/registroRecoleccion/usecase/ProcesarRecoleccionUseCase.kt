package ucne.edu.rocash.domain.registroRecoleccion.usecase

import ucne.edu.rocash.domain.agenteVentas.usecase.GetAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.UpsertAgenteUseCase
import ucne.edu.rocash.domain.registroRecoleccion.model.EstadoVisita
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository
import javax.inject.Inject

class ProcesarRecoleccionUseCase @Inject constructor(
    private val repository: RegistroRecoleccionRepository,
    private val getAgenteUseCase: GetAgenteUseCase,
    private val upsertAgenteUseCase: UpsertAgenteUseCase
) {
    suspend operator fun invoke(
        hojaRutaId: Int,
        estacionId: Int,
        agenteId1: Int,
        agenteId2: Int?,
        ventaBrutaStr: String,
        comisionClienteStr: String,
        montoRecolectadoStr: String
    ): Result<Int> {
        val vbResult = validateMontoNumerico(ventaBrutaStr, "Venta Bruta")
        if (!vbResult.isValid) return Result.failure(IllegalArgumentException(vbResult.error))

        val ccResult = validateMontoNumerico(comisionClienteStr, "Comisión Cliente")
        if (!ccResult.isValid) return Result.failure(IllegalArgumentException(ccResult.error))

        val mrResult = validateMontoNumerico(montoRecolectadoStr, "Monto Recolectado")
        if (!mrResult.isValid) return Result.failure(IllegalArgumentException(mrResult.error))

        val ventaBruta = ventaBrutaStr.toDouble()
        val comisionCliente = comisionClienteStr.toDouble()
        val montoRecolectado = montoRecolectadoStr.toDouble()

        val montoEsperado = ventaBruta - comisionCliente
        val deudaGenerada = if (montoRecolectado < montoEsperado) montoEsperado - montoRecolectado else 0.0

        val registro = RegistroRecoleccion(
            hojaRutaId = hojaRutaId,
            estacionId = estacionId,
            ventaBruta = ventaBruta,
            comisionCliente = comisionCliente,
            montoRecolectado = montoRecolectado,
            montoEsperado = montoEsperado,
            montoDeuda = deudaGenerada,
            estadoVisita = EstadoVisita.COMPLETADA
        )

        return runCatching {
            val newId = repository.upsert(registro)

            if (deudaGenerada > 0) {
                if (agenteId2 != null) {
                    val deudaMitad = deudaGenerada / 2
                    sumarDeuda(agenteId1, deudaMitad)
                    sumarDeuda(agenteId2, deudaMitad)
                } else {
                    sumarDeuda(agenteId1, deudaGenerada)
                }
            }
            newId
        }
    }

    private suspend fun sumarDeuda(agenteId: Int, monto: Double) {
        val agente = getAgenteUseCase(agenteId)
        if (agente != null) {
            val agenteActualizado = agente.copy(deudaAcumulada = agente.deudaAcumulada + monto)
            upsertAgenteUseCase(agenteActualizado)
        }
    }
}