package ucne.edu.rocash.domain.usecase

import ucne.edu.rocash.domain.model.EstadoVisita
import ucne.edu.rocash.domain.model.RegistroRecoleccion
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

class ProcesarRecoleccionEstacionUseCase @Inject constructor(
    private val repository: RoCashRepository
) {
    suspend operator fun invoke(
        hojaRutaId: Int,
        estacionId: Int,
        agenteId: Int,
        ventaBruta: Double,
        porcentajeCliente: Double,
        montoRecolectado: Double
    ) {
        val montoEsperado = ventaBruta - porcentajeCliente
        val deudaGenerada = if (montoRecolectado < montoEsperado) {
            montoEsperado - montoRecolectado
        } else 0.0

        val registro = RegistroRecoleccion(
            hojaRutaId = hojaRutaId,
            estacionId = estacionId,
            ventaBruta = ventaBruta,
            porcentajeCliente = porcentajeCliente,
            montoRecolectado = montoRecolectado,
            montoEsperado = montoEsperado,
            montoDeuda = deudaGenerada,
            estadoVisita = EstadoVisita.COMPLETADA
        )

        repository.guardarRegistroRecoleccion(registro)

        if (deudaGenerada > 0) {
            repository.actualizarDeudaAgente(agenteId, deudaGenerada)
        }
    }
}