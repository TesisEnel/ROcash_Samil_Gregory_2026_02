package ucne.edu.rocash.domain.registroRecoleccion.usecase

import ucne.edu.rocash.domain.agenteVentas.usecase.SumarDeudaAgenteUseCase
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import ucne.edu.rocash.domain.registroRecoleccion.model.EstadoVisita
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository
import javax.inject.Inject

class ProcesarRecoleccionUseCase @Inject constructor(
    private val repository: RegistroRecoleccionRepository,
    private val hojaRutaRepository: HojaRutaRepository,
    private val sumarDeudaAgenteUseCase: SumarDeudaAgenteUseCase
) {
    suspend operator fun invoke(
        hojaRutaId: Int,
        estacionId: Int,
        agenteId1: Int,
        agenteId2: Int?,
        ventaBrutaStr: String,
        comisionClienteStr: String,
        montoRecolectadoStr: String,
        notaIncidencia: String? = null
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

        val coherencia = validateCoherenciaCuadre(ventaBruta, comisionCliente, montoRecolectado)
        if (!coherencia.isValid) {
            return Result.failure(IllegalArgumentException(coherencia.error))
        }

        return runCatching {
            val ruta = hojaRutaRepository.obtenerRuta(hojaRutaId)
                ?: throw IllegalStateException("La hoja de ruta no existe")

            if (ruta.estado == EstadoRuta.CERRADA) {
                throw IllegalStateException("La ruta ya está cerrada; no admite más cuadres")
            }

            val previo = repository.obtenerPorRutaYEstacion(hojaRutaId, estacionId)

            val montoEsperado = ventaBruta - comisionCliente
            val deuda = (montoEsperado - montoRecolectado).coerceAtLeast(0.0)

            val registro = RegistroRecoleccion(
                // Conservar el id existente convierte el insert en update.
                recoleccionId = previo?.recoleccionId ?: 0,
                hojaRutaId = hojaRutaId,
                estacionId = estacionId,
                ventaBruta = ventaBruta,
                comisionCliente = comisionCliente,
                montoRecolectado = montoRecolectado,
                montoEsperado = montoEsperado,
                montoDeuda = deuda,
                estadoVisita = EstadoVisita.COMPLETADA,
                notaIncidencia = notaIncidencia
            )

            val nuevoId = repository.upsert(registro)

            // Si se esta corrigiendo un cuadre anterior solo se aplica la
            // diferencia, para no cobrarle la deuda dos veces al agente.
            val deudaDelta = deuda - (previo?.montoDeuda ?: 0.0)
            if (deudaDelta != 0.0) {
                if (agenteId2 != null) {
                    val mitad = deudaDelta / 2
                    sumarDeudaAgenteUseCase(agenteId1, mitad)
                    sumarDeudaAgenteUseCase(agenteId2, mitad)
                } else {
                    sumarDeudaAgenteUseCase(agenteId1, deudaDelta)
                }
            }

            hojaRutaRepository.marcarEstadoEstacion(
                rutaId = hojaRutaId,
                estacionId = estacionId,
                estado = EstadoVisitaEstacion.COMPLETADA
            )

            if (ruta.estado == EstadoRuta.PENDIENTE) {
                hojaRutaRepository.cambiarEstadoRuta(hojaRutaId, EstadoRuta.EN_PROGRESO)
            }

            nuevoId
        }
    }
}