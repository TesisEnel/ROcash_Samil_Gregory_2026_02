package ucne.edu.rocash.domain.registroRecoleccion.usecase

import ucne.edu.rocash.domain.registroRecoleccion.model.CalculoCuadre
import javax.inject.Inject
class CalcularCuadreUseCase @Inject constructor() {

    operator fun invoke(
        ventaBruta: Double,
        comisionCliente: Double,
        montoRecolectado: Double
    ): CalculoCuadre = CalculoCuadre.desde(
        ventaBruta = ventaBruta,
        comisionCliente = comisionCliente,
        montoRecolectado = montoRecolectado
    )

    fun desdeTexto(
        ventaBruta: String,
        comisionCliente: String,
        montoRecolectado: String
    ): CalculoCuadre = CalculoCuadre.desdeTexto(
        ventaBruta = ventaBruta,
        comisionCliente = comisionCliente,
        montoRecolectado = montoRecolectado
    )
}
