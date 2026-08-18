package ucne.edu.rocash.presentation.hojaRuta.crear

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

class CrearRutaReducerTest {

    private val disponibles = listOf(
        EstacionVentas(1, "Banca Norte", "Av. Principal 12", agenteId = 1),
        EstacionVentas(2, "Banca Sur", "Calle 8 esq. Duarte", agenteId = 2),
        EstacionVentas(3, "Banca Central", "Parque Duarte", agenteId = 1)
    )

    private fun estadoBase() = CrearRutaReducer.conEstaciones(
        estado = CrearRutaUiState(),
        disponibles = disponibles,
        comprometidas = setOf(3)
    )

    @Test
    fun `proyecta una fila por estacion con sus banderas resueltas`() {
        val estado = CrearRutaReducer.conEstacionAlternada(estadoBase(), estacionId = 1)

        assertEquals(3, estado.estaciones.size)
        assertTrue(estado.estaciones.first { it.estacion.estacionId == 1 }.seleccionada)
        assertFalse(estado.estaciones.first { it.estacion.estacionId == 2 }.seleccionada)
        assertTrue(estado.estaciones.first { it.estacion.estacionId == 3 }.comprometida)
    }

    @Test
    fun `alternar dos veces deja la estacion sin seleccionar`() {
        val estado = CrearRutaReducer.conEstacionAlternada(
            CrearRutaReducer.conEstacionAlternada(estadoBase(), estacionId = 1),
            estacionId = 1
        )

        assertEquals(0, estado.cantidadSeleccionada)
        assertFalse(estado.haySeleccion)
        assertFalse(estado.puedeGuardar)
    }

    @Test
    fun `una estacion comprometida no se puede seleccionar`() {
        val estado = CrearRutaReducer.conEstacionAlternada(estadoBase(), estacionId = 3)

        assertEquals(0, estado.cantidadSeleccionada)
    }

    @Test
    fun `una estacion que pasa a comprometida se retira de la seleccion`() {
        val conSeleccion = CrearRutaReducer.conEstacionAlternada(estadoBase(), estacionId = 2)
        assertEquals(1, conSeleccion.cantidadSeleccionada)

        val despues = CrearRutaReducer.conEstaciones(
            estado = conSeleccion,
            disponibles = disponibles,
            comprometidas = setOf(2, 3)
        )

        assertEquals(0, despues.cantidadSeleccionada)
        assertFalse(despues.puedeGuardar)
    }

    @Test
    fun `no permite guardar mientras esta guardando`() {
        val estado = CrearRutaReducer.guardando(
            CrearRutaReducer.conEstacionAlternada(estadoBase(), estacionId = 1)
        )

        assertTrue(estado.isSaving)
        assertFalse(estado.puedeGuardar)
    }

    @Test
    fun `limpiar seleccion vacia los contadores`() {
        val estado = CrearRutaReducer.sinSeleccion(
            CrearRutaReducer.conEstacionAlternada(estadoBase(), estacionId = 1)
        )

        assertEquals(0, estado.cantidadSeleccionada)
        assertTrue(estado.hayEstaciones)
    }

    @Test
    fun `crear la ruta enciende la bandera de navegacion con su id`() {
        val estado = CrearRutaReducer.rutaCreada(
            estado = CrearRutaReducer.guardando(
                CrearRutaReducer.conEstacionAlternada(estadoBase(), estacionId = 1)
            ),
            rutaId = 42
        )

        assertEquals(42, estado.rutaCreadaId)
        assertFalse(estado.isSaving)
    }

    @Test
    fun `un fallo al crear deja el mensaje y no navega`() {
        val estado = CrearRutaReducer.guardadoFallido(
            estado = CrearRutaReducer.guardando(
                CrearRutaReducer.conEstacionAlternada(estadoBase(), estacionId = 1)
            ),
            mensaje = "Una banca ya está en otra ruta"
        )

        assertNull(estado.rutaCreadaId)
        assertEquals("Una banca ya está en otra ruta", estado.errorMessage)
    }

    @Test
    fun `sin estaciones marca la lista como vacia`() {
        val estado = CrearRutaReducer.conEstaciones(
            estado = CrearRutaUiState(),
            disponibles = emptyList(),
            comprometidas = emptySet()
        )

        assertFalse(estado.hayEstaciones)
        assertFalse(estado.isLoading)
    }
}
