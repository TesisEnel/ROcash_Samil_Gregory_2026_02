package ucne.edu.rocash.domain.validations

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ucne.edu.rocash.domain.abonoDeuda.usecase.validateMontoAbono
import ucne.edu.rocash.domain.agenteVentas.usecase.validateNombre
import ucne.edu.rocash.domain.agenteVentas.usecase.validateTelefono
import ucne.edu.rocash.domain.estacion.usecase.validateAgenteAsignado
import ucne.edu.rocash.domain.estacion.usecase.validateEstacionDireccion
import ucne.edu.rocash.domain.estacion.usecase.validateEstacionNombre
import ucne.edu.rocash.domain.hojaRuta.usecase.MAX_ESTACIONES_POR_RUTA
import ucne.edu.rocash.domain.hojaRuta.usecase.validateEstacionesLibres
import ucne.edu.rocash.domain.hojaRuta.usecase.validateEstacionesSeleccionadas
import ucne.edu.rocash.domain.hojaRuta.usecase.validateRecolectorId
import ucne.edu.rocash.domain.registroRecoleccion.usecase.validateCoherenciaCuadre
import ucne.edu.rocash.domain.registroRecoleccion.usecase.validateMontoNumerico

/**
 * Todas las validaciones del dominio en un solo archivo.
 *
 * Son funciones puras: sin corrutinas, sin mocks y sin Android. Son también la
 * primera barrera contra datos malos, así que no tenerlas cubiertas dejaba sin
 * red justo la capa más barata de probar.
 *
 * `validateEmail` y `validatePassword` de auth quedan fuera a propósito:
 * dependen de `android.util.Patterns` y necesitan Robolectric. Están en
 * AuthValidationsTest.
 */
class ValidacionesTest {

    // ---------- Montos de recolección ----------

    @Test
    fun `un monto vacio se rechaza nombrando el campo`() {
        val resultado = validateMontoNumerico("", "Venta Bruta")

        assertFalse(resultado.isValid)
        assertEquals("El campo Venta Bruta es obligatorio", resultado.error)
    }

    @Test
    fun `un monto no numerico se rechaza`() {
        assertFalse(validateMontoNumerico("mil", "Venta Bruta").isValid)
        assertFalse(validateMontoNumerico("1,000", "Venta Bruta").isValid)
    }

    @Test
    fun `un monto negativo se rechaza`() {
        assertFalse(validateMontoNumerico("-1", "Venta Bruta").isValid)
    }

    @Test
    fun `cero es un monto valido`() {
        // Una banca puede cerrar el día sin ventas; eso no es un error.
        assertTrue(validateMontoNumerico("0", "Venta Bruta").isValid)
    }

    @Test
    fun `acepta decimales`() {
        assertTrue(validateMontoNumerico("1500.75", "Venta Bruta").isValid)
    }

    // ---------- Coherencia del cuadre ----------

    @Test
    fun `la comision no puede superar la venta bruta`() {
        val resultado = validateCoherenciaCuadre(1_000.0, 3_000.0, 0.0)

        assertFalse(resultado.isValid)
    }

    @Test
    fun `no se puede recolectar mas de lo esperado`() {
        val resultado = validateCoherenciaCuadre(10_000.0, 2_000.0, 9_000.0)

        assertFalse(resultado.isValid)
    }

    @Test
    fun `recolectar exactamente lo esperado es valido`() {
        // El caso frontera: 10000 - 2000 = 8000. No debe rechazarse.
        assertTrue(validateCoherenciaCuadre(10_000.0, 2_000.0, 8_000.0).isValid)
    }

    @Test
    fun `una comision igual a la venta bruta es valida`() {
        assertTrue(validateCoherenciaCuadre(5_000.0, 5_000.0, 0.0).isValid)
    }

    // ---------- Hoja de ruta ----------

    @Test
    fun `sin recolector autenticado no se puede crear una ruta`() {
        assertFalse(validateRecolectorId(null).isValid)
        assertFalse(validateRecolectorId("").isValid)
        assertFalse(validateRecolectorId("   ").isValid)
        assertTrue(validateRecolectorId("uid-cobrador").isValid)
    }

    @Test
    fun `una ruta sin estaciones se rechaza`() {
        assertFalse(validateEstacionesSeleccionadas(emptyList()).isValid)
    }

    @Test
    fun `una ruta con estaciones repetidas se rechaza`() {
        assertFalse(validateEstacionesSeleccionadas(listOf(1, 2, 1)).isValid)
    }

    @Test
    fun `el tope de estaciones por ruta se respeta en el limite`() {
        val justo = (1..MAX_ESTACIONES_POR_RUTA).toList()
        val unaDeMas = (1..MAX_ESTACIONES_POR_RUTA + 1).toList()

        assertTrue(validateEstacionesSeleccionadas(justo).isValid)
        assertFalse(validateEstacionesSeleccionadas(unaDeMas).isValid)
    }

    @Test
    fun `una estacion ya comprometida bloquea la ruta y se nombra`() {
        val resultado = validateEstacionesLibres(listOf(3, 7))

        assertFalse(resultado.isValid)
        assertTrue(resultado.error!!.contains("3"))
        assertTrue(resultado.error!!.contains("7"))
    }

    @Test
    fun `sin estaciones comprometidas la ruta puede crearse`() {
        assertTrue(validateEstacionesLibres(emptyList()).isValid)
    }

    // ---------- Estación ----------

    @Test
    fun `el nombre de una banca exige al menos tres caracteres`() {
        assertFalse(validateEstacionNombre("").isValid)
        assertFalse(validateEstacionNombre("AB").isValid)
        assertTrue(validateEstacionNombre("Sur").isValid)
    }

    @Test
    fun `la direccion de una banca no puede estar vacia`() {
        assertFalse(validateEstacionDireccion("   ").isValid)
        assertTrue(validateEstacionDireccion("Calle 8 esq. Duarte").isValid)
    }

    @Test
    fun `una banca sin agente se rechaza`() {
        // El id 0 cuenta como "sin asignar": es el valor por defecto de Room
        // antes de que exista la fila.
        assertFalse(validateAgenteAsignado(null).isValid)
        assertFalse(validateAgenteAsignado(0).isValid)
        assertTrue(validateAgenteAsignado(1).isValid)
    }

    // ---------- Agente ----------

    @Test
    fun `el nombre del agente exige al menos tres caracteres`() {
        assertFalse(validateNombre("").isValid)
        assertFalse(validateNombre("Jo").isValid)
        assertTrue(validateNombre("Ramón").isValid)
    }

    @Test
    fun `el telefono del agente exige al menos diez digitos`() {
        assertFalse(validateTelefono("").isValid)
        assertFalse(validateTelefono("809555").isValid)
        assertTrue(validateTelefono("8095551234").isValid)
    }

    // ---------- Abonos ----------

    @Test
    fun `un abono vacio o no numerico se rechaza`() {
        assertFalse(validateMontoAbono("", 5_000.0).isValid)
        assertFalse(validateMontoAbono("mil pesos", 5_000.0).isValid)
    }

    @Test
    fun `un abono de cero o negativo se rechaza`() {
        assertFalse(validateMontoAbono("0", 5_000.0).isValid)
        assertFalse(validateMontoAbono("-100", 5_000.0).isValid)
    }

    @Test
    fun `no se puede abonar a quien no debe nada`() {
        assertFalse(validateMontoAbono("100", 0.0).isValid)
    }

    @Test
    fun `un abono mayor que la deuda se rechaza y sugiere saldar`() {
        val resultado = validateMontoAbono("9000", 5_000.0)

        assertFalse(resultado.isValid)
        assertTrue(resultado.error!!.contains("Saldar"))
    }

    @Test
    fun `un abono igual a la deuda es valido`() {
        // Caso frontera: pagar justo el total debe pasar por la vía del abono,
        // no obligar al botón Saldar.
        assertTrue(validateMontoAbono("5000", 5_000.0).isValid)
    }
}
