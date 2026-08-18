package ucne.edu.rocash.presentation.hojaRuta.cuadre

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ucne.edu.rocash.domain.registroRecoleccion.model.CalculoCuadre

/**
 * El reducer es una función pura: estos tests no necesitan corrutinas, ni Hilt,
 * ni mocks, ni un dispatcher de prueba. Esa es la ganancia concreta de haber
 * sacado las derivaciones del UiState.
 */
class CuadreReducerTest {

    private val estadoCargado = CuadreUiState(isLoading = false)

    @Test
    fun `no permite guardar mientras falte algun monto`() {
        val estado = CuadreReducer.conMontosDeTexto(
            estado = estadoCargado,
            ventaBruta = "5000",
            comisionCliente = "1000",
            montoRecolectado = ""
        )

        assertFalse(estado.puedeGuardar)
    }

    @Test
    fun `permite guardar con los tres montos presentes`() {
        val estado = CuadreReducer.conMontosDeTexto(
            estado = estadoCargado,
            ventaBruta = "5000",
            comisionCliente = "1000",
            montoRecolectado = "4000"
        )

        assertTrue(estado.puedeGuardar)
    }

    @Test
    fun `no permite guardar mientras esta guardando`() {
        val estado = CuadreReducer.guardando(
            CuadreReducer.conMontosDeTexto(estadoCargado, "5000", "1000", "4000")
        )

        assertTrue(estado.isSaving)
        assertFalse(estado.puedeGuardar)
    }

    @Test
    fun `deposita el calculo del dominio sin recalcularlo`() {
        val calculo = CalculoCuadre.desdeTexto("5000", "1000", "3500")

        val estado = CuadreReducer.conMontos(
            estado = estadoCargado,
            campoEditado = CampoMonto.MONTO_RECOLECTADO,
            ventaBruta = "5000",
            comisionCliente = "1000",
            montoRecolectado = "3500",
            calculo = calculo
        )

        assertEquals(calculo.montoEsperado, estado.montoEsperado, 0.0)
        assertEquals(calculo.montoDeuda, estado.deudaGenerada, 0.0)
        assertTrue(estado.hayDeuda)
    }

    @Test
    fun `sin deuda no marca la bandera de deuda`() {
        val estado = CuadreReducer.conMontosDeTexto(estadoCargado, "5000", "1000", "4000")

        assertEquals(0.0, estado.deudaGenerada, 0.0)
        assertFalse(estado.hayDeuda)
        assertFalse(estado.deudaSeReparte)
    }

    @Test
    fun `la deuda se reparte solo si la banca tiene segundo agente`() {
        val conUnAgente = CuadreReducer.conMontosDeTexto(
            estado = estadoCargado,
            ventaBruta = "10000",
            comisionCliente = "2000",
            montoRecolectado = "5000"
        )
        val conDosAgentes = CuadreReducer.conMontosDeTexto(
            estado = estadoCargado.copy(agenteId2 = 4),
            ventaBruta = "10000",
            comisionCliente = "2000",
            montoRecolectado = "5000"
        )

        assertFalse(conUnAgente.deudaSeReparte)
        assertTrue(conDosAgentes.deudaSeReparte)
    }

    @Test
    fun `guardar con exito enciende la bandera de navegacion`() {
        val estado = CuadreReducer.guardadoExitoso(
            CuadreReducer.guardando(
                CuadreReducer.conMontosDeTexto(estadoCargado, "5000", "1000", "4000")
            )
        )

        assertTrue(estado.saved)
        assertFalse(estado.isSaving)
        assertNull(estado.errorMessage)
    }

    @Test
    fun `guardar con fallo deja el mensaje y no navega`() {
        val estado = CuadreReducer.guardadoFallido(
            estado = CuadreReducer.guardando(
                CuadreReducer.conMontosDeTexto(estadoCargado, "5000", "1000", "4000")
            ),
            mensaje = "La ruta ya está cerrada"
        )

        assertFalse(estado.saved)
        assertFalse(estado.isSaving)
        assertEquals("La ruta ya está cerrada", estado.errorMessage)
    }

    @Test
    fun `ErrorMostrado apaga el mensaje`() {
        val estado = CuadreReducer.sinMensaje(
            CuadreReducer.guardadoFallido(estadoCargado, "boom")
        )

        assertNull(estado.errorMessage)
    }

    @Test
    fun `editar un campo limpia solo su propio error`() {
        val conErrores = CuadreReducer.conErroresDeValidacion(
            estado = estadoCargado,
            ventaBrutaError = "El valor debe ser numérico",
            comisionError = "El valor debe ser numérico",
            montoRecolectadoError = "El valor debe ser numérico"
        )

        val despues = CuadreReducer.conMontos(
            estado = conErrores,
            campoEditado = CampoMonto.VENTA_BRUTA,
            ventaBruta = "5000",
            comisionCliente = "x",
            montoRecolectado = "x",
            calculo = CalculoCuadre.desdeTexto("5000", "x", "x")
        )

        assertNull(despues.ventaBrutaError)
        assertEquals("El valor debe ser numérico", despues.comisionError)
        assertEquals("El valor debe ser numérico", despues.montoRecolectadoError)
    }
}
