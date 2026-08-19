package ucne.edu.rocash.domain.registroRecoleccion

import org.junit.Assert.assertEquals
import org.junit.Test
import ucne.edu.rocash.domain.registroRecoleccion.model.CalculoCuadre

/**
 * La fórmula del cuadre no tenía un solo test, y es la aritmética de la que
 * cuelga todo lo demás: lo que se le muestra al cobrador en pantalla, lo que se
 * guarda en el registro y lo que se le carga como deuda al agente.
 *
 * Es una función pura, así que estos casos no necesitan corrutinas ni mocks.
 */
class CalculoCuadreTest {

    @Test
    fun `el monto esperado es la venta bruta menos la comision`() {
        val calculo = CalculoCuadre.desde(
            ventaBruta = 10_000.0,
            comisionCliente = 2_000.0,
            montoRecolectado = 8_000.0
        )

        assertEquals(8_000.0, calculo.montoEsperado, 0.0)
    }

    @Test
    fun `recolectar todo lo esperado no genera deuda`() {
        val calculo = CalculoCuadre.desde(10_000.0, 2_000.0, 8_000.0)

        assertEquals(0.0, calculo.montoDeuda, 0.0)
    }

    @Test
    fun `recolectar de menos genera deuda por la diferencia`() {
        val calculo = CalculoCuadre.desde(10_000.0, 2_000.0, 5_000.0)

        assertEquals(3_000.0, calculo.montoDeuda, 0.0)
    }

    @Test
    fun `recolectar de mas no genera deuda negativa`() {
        // coerceAtLeast(0.0) evita que un sobrante se convierta en un credito
        // silencioso a favor del agente.
        val calculo = CalculoCuadre.desde(10_000.0, 2_000.0, 9_000.0)

        assertEquals(0.0, calculo.montoDeuda, 0.0)
    }

    @Test
    fun `una comision mayor que la venta deja el esperado en negativo`() {
        // El calculo no valida: eso es trabajo de validateCoherenciaCuadre.
        // Se fija el comportamiento para que nadie lo cambie por accidente.
        val calculo = CalculoCuadre.desde(1_000.0, 3_000.0, 0.0)

        assertEquals(-2_000.0, calculo.montoEsperado, 0.0)
        assertEquals(0.0, calculo.montoDeuda, 0.0)
    }

    @Test
    fun `una banca sin movimiento no genera nada`() {
        val calculo = CalculoCuadre.desde(0.0, 0.0, 0.0)

        assertEquals(0.0, calculo.montoEsperado, 0.0)
        assertEquals(0.0, calculo.montoDeuda, 0.0)
    }

    @Test
    fun `desdeTexto convierte los tres campos`() {
        val calculo = CalculoCuadre.desdeTexto("10000", "2000", "5000")

        assertEquals(10_000.0, calculo.ventaBruta, 0.0)
        assertEquals(8_000.0, calculo.montoEsperado, 0.0)
        assertEquals(3_000.0, calculo.montoDeuda, 0.0)
    }

    @Test
    fun `desdeTexto trata lo no numerico como cero y no revienta`() {
        // El formulario llama a desdeTexto en cada tecla, incluso con el campo
        // a medio escribir. Si lanzara excepcion, la vista previa en vivo del
        // cuadre tumbaria la pantalla mientras el usuario teclea.
        val calculo = CalculoCuadre.desdeTexto("abc", "", "5000")

        assertEquals(0.0, calculo.ventaBruta, 0.0)
        assertEquals(0.0, calculo.comisionCliente, 0.0)
        assertEquals(5_000.0, calculo.montoRecolectado, 0.0)
        assertEquals(0.0, calculo.montoDeuda, 0.0)
    }

    @Test
    fun `acepta decimales sin perder centavos`() {
        val calculo = CalculoCuadre.desdeTexto("1500.75", "300.25", "1000.50")

        assertEquals(1_200.50, calculo.montoEsperado, 0.001)
        assertEquals(200.0, calculo.montoDeuda, 0.001)
    }
}
