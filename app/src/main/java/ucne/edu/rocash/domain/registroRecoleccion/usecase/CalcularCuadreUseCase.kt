package ucne.edu.rocash.domain.registroRecoleccion.usecase

import ucne.edu.rocash.domain.registroRecoleccion.model.CalculoCuadre
import javax.inject.Inject

/**
 * Expone la fórmula del cuadre como dependencia inyectable.
 *
 * No tiene dependencias propias a propósito: es un cálculo puro. Existe como
 * clase (y no como función suelta) para que CuadreViewModel lo reciba por
 * constructor y los tests puedan sustituirlo o verificarlo igual que cualquier
 * otro caso de uso.
 */
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

    /** Para el preview en vivo del formulario, donde los campos son texto. */
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
