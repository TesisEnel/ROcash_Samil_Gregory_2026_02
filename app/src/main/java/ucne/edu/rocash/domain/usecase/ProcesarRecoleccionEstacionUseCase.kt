package ucne.edu.rocash.domain.usecase

import ucne.edu.rocash.domain.model.EstadoVisita
import ucne.edu.rocash.domain.model.RegistroRecoleccion
import ucne.edu.rocash.domain.repository.RoCashRepository
import java.util.UUID
import javax.inject.Inject

class ProcesarRecoleccionEstacionUseCase @Inject constructor(
    private val repository: RoCashRepository
) {
    suspend operator fun invoke(
        hojaRutaId: Int,
        estacionId: String,
        agenteId1: String,
        agenteId2: String?,
        ventaBruta: Double,
        comisionCliente: Double,
        montoRecolectado: Double
    ) {
        val montoEsperado = ventaBruta - comisionCliente
        val deudaGenerada = if (montoRecolectado < montoEsperado) montoEsperado - montoRecolectado else 0.0

        val registro = RegistroRecoleccion(
            id = UUID.randomUUID().toString(),
            hojaRutaId = hojaRutaId,
            estacionId = estacionId,
            ventaBruta = ventaBruta,
            comisionCliente = comisionCliente,
            montoRecolectado = montoRecolectado,
            montoEsperado = montoEsperado,
            montoDeuda = deudaGenerada,
            estadoVisita = EstadoVisita.COMPLETADA
        )

        repository.guardarRegistroRecoleccion(registro)

        if (deudaGenerada > 0) {
            if (agenteId2 != null) {
                val deudaMitad = deudaGenerada / 2
                repository.sumarDeudaAgente(agenteId1, deudaMitad)
                repository.sumarDeudaAgente(agenteId2, deudaMitad)
            } else {
                repository.sumarDeudaAgente(agenteId1, deudaGenerada)
            }
        }

    }
}