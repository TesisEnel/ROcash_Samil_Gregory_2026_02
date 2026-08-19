package ucne.edu.rocash.domain.hojaRuta

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.hojaRuta.model.EstacionEnRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import ucne.edu.rocash.domain.hojaRuta.usecase.CerrarHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.CrearHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.OmitirEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository

/**
 * Ciclo de vida completo de una hoja de ruta: crearla, omitir una banca y
 * cerrarla. Ninguno de los tres casos de uso tenía tests.
 *
 * El cierre es el más delicado: graba cuatro totales que quedan fijos para
 * siempre en el historial, y una vez cerrada la ruta no admite correcciones.
 */
class HojaRutaUseCasesTest {

    private lateinit var hojaRutaRepository: HojaRutaRepository
    private lateinit var registroRepository: RegistroRecoleccionRepository

    private lateinit var crearRuta: CrearHojaRutaUseCase
    private lateinit var cerrarRuta: CerrarHojaRutaUseCase
    private lateinit var omitirEstacion: OmitirEstacionUseCase

    private fun bancaEnRuta(id: Int, estado: EstadoVisitaEstacion) = EstacionEnRuta(
        estacion = EstacionVentas(
            estacionId = id,
            nombre = "Banca $id",
            direccion = "Calle $id",
            agenteId = 1
        ),
        estado = estado
    )

    private val rutaCompleta = HojaRuta(
        id = 7,
        recolectorId = "uid-cobrador",
        estado = EstadoRuta.EN_PROGRESO,
        estaciones = listOf(
            bancaEnRuta(1, EstadoVisitaEstacion.COMPLETADA),
            bancaEnRuta(2, EstadoVisitaEstacion.OMITIDA)
        )
    )

    private val resumen = ResumenRecoleccionRuta(
        totalVentaBruta = 20_000.0,
        totalComisionClientes = 4_000.0,
        totalRecaudado = 14_000.0,
        totalDeudas = 2_000.0,
        cantidadRegistros = 1
    )

    @Before
    fun setup() {
        hojaRutaRepository = mockk(relaxed = true)
        registroRepository = mockk(relaxed = true)

        crearRuta = CrearHojaRutaUseCase(hojaRutaRepository)
        cerrarRuta = CerrarHojaRutaUseCase(hojaRutaRepository, registroRepository)
        omitirEstacion = OmitirEstacionUseCase(hojaRutaRepository)

        coEvery { hojaRutaRepository.obtenerRuta(7) } returns rutaCompleta
        coEvery { hojaRutaRepository.estacionesYaComprometidas(any()) } returns emptyList()
        coEvery { hojaRutaRepository.crearRutaConEstaciones(any(), any()) } returns 7
        coEvery { registroRepository.obtenerResumenDeRuta(7) } returns resumen
    }

    // ---------- Crear ----------

    @Test
    fun `crear una ruta valida devuelve su id`() = runTest {
        val resultado = crearRuta(recolectorId = "uid-cobrador", estacionIds = listOf(1, 2, 3))

        assertTrue(resultado.isSuccess)
        assertEquals(7, resultado.getOrThrow())
    }

    @Test
    fun `la ruta nace en PENDIENTE y con el recolector de la sesion`() = runTest {
        crearRuta(recolectorId = "uid-cobrador", estacionIds = listOf(1))

        coVerify(exactly = 1) {
            hojaRutaRepository.crearRutaConEstaciones(
                match {
                    it.estado == EstadoRuta.PENDIENTE && it.recolectorId == "uid-cobrador"
                },
                listOf(1)
            )
        }
    }

    @Test
    fun `sin sesion no se puede crear una ruta`() = runTest {
        val resultado = crearRuta(recolectorId = null, estacionIds = listOf(1))

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { hojaRutaRepository.crearRutaConEstaciones(any(), any()) }
    }

    @Test
    fun `una ruta sin bancas no se crea`() = runTest {
        val resultado = crearRuta(recolectorId = "uid-cobrador", estacionIds = emptyList())

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { hojaRutaRepository.crearRutaConEstaciones(any(), any()) }
    }

    @Test
    fun `una banca ya asignada a otra ruta abierta bloquea la creacion`() = runTest {
        // Es la unica defensa contra que dos cobradores cuadren la misma banca
        // el mismo dia.
        coEvery { hojaRutaRepository.estacionesYaComprometidas(any()) } returns listOf(2)

        val resultado = crearRuta(recolectorId = "uid-cobrador", estacionIds = listOf(1, 2))

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull()!!.message!!.contains("2"))
        coVerify(exactly = 0) { hojaRutaRepository.crearRutaConEstaciones(any(), any()) }
    }

    // ---------- Omitir ----------

    @Test
    fun `omitir marca la banca como OMITIDA y no como completada`() = runTest {
        val resultado = omitirEstacion(rutaId = 7, estacionId = 1)

        assertTrue(resultado.isSuccess)
        coVerify(exactly = 1) {
            hojaRutaRepository.marcarEstadoEstacion(7, 1, EstadoVisitaEstacion.OMITIDA)
        }
    }

    @Test
    fun `omitir la primera banca mueve la ruta a EN_PROGRESO`() = runTest {
        coEvery { hojaRutaRepository.obtenerRuta(7) } returns
                rutaCompleta.copy(estado = EstadoRuta.PENDIENTE)

        omitirEstacion(rutaId = 7, estacionId = 1)

        coVerify(exactly = 1) {
            hojaRutaRepository.cambiarEstadoRuta(7, EstadoRuta.EN_PROGRESO)
        }
    }

    @Test
    fun `no se puede omitir en una ruta cerrada`() = runTest {
        coEvery { hojaRutaRepository.obtenerRuta(7) } returns
                rutaCompleta.copy(estado = EstadoRuta.CERRADA)

        val resultado = omitirEstacion(rutaId = 7, estacionId = 1)

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { hojaRutaRepository.marcarEstadoEstacion(any(), any(), any()) }
    }

    // ---------- Cerrar ----------

    @Test
    fun `cerrar una ruta sin pendientes graba los totales del resumen`() = runTest {
        val resultado = cerrarRuta(rutaId = 7)

        assertTrue(resultado.isSuccess)
        coVerify(exactly = 1) {
            hojaRutaRepository.cerrarRuta(
                rutaId = 7,
                fechaCierre = any(),
                totalVentaBruta = 20_000.0,
                totalComisionClientes = 4_000.0,
                totalRecaudado = 14_000.0,
                totalDeudas = 2_000.0
            )
        }
    }

    @Test
    fun `la ruta devuelta queda CERRADA con sus totales y fecha`() = runTest {
        val cerrada = cerrarRuta(rutaId = 7).getOrThrow()

        assertEquals(EstadoRuta.CERRADA, cerrada.estado)
        assertEquals(14_000.0, cerrada.totalRecaudado, 0.0)
        assertEquals(2_000.0, cerrada.totalDeudas, 0.0)
        assertTrue(cerrada.fechaCierre != null)
    }

    @Test
    fun `una banca omitida no impide cerrar la ruta`() = runTest {
        // Omitir es una decisión válida del cobrador: la banca estaba cerrada,
        // el agente no apareció. No debe bloquear el cierre de la jornada.
        assertTrue(cerrarRuta(rutaId = 7).isSuccess)
    }

    @Test
    fun `una banca pendiente impide cerrar y el mensaje la nombra`() = runTest {
        coEvery { hojaRutaRepository.obtenerRuta(7) } returns rutaCompleta.copy(
            estaciones = rutaCompleta.estaciones + bancaEnRuta(3, EstadoVisitaEstacion.PENDIENTE)
        )

        val resultado = cerrarRuta(rutaId = 7)

        assertTrue(resultado.isFailure)
        // El cobrador necesita saber cuál le falta, no solo que falta algo.
        assertTrue(resultado.exceptionOrNull()!!.message!!.contains("Banca 3"))
        coVerify(exactly = 0) {
            hojaRutaRepository.cerrarRuta(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `una ruta ya cerrada no se cierra dos veces`() = runTest {
        coEvery { hojaRutaRepository.obtenerRuta(7) } returns
                rutaCompleta.copy(estado = EstadoRuta.CERRADA)

        val resultado = cerrarRuta(rutaId = 7)

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) {
            hojaRutaRepository.cerrarRuta(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `una ruta sin bancas asignadas no se cierra`() = runTest {
        coEvery { hojaRutaRepository.obtenerRuta(7) } returns
                rutaCompleta.copy(estaciones = emptyList())

        assertTrue(cerrarRuta(rutaId = 7).isFailure)
    }

    @Test
    fun `una ruta inexistente falla sin tocar nada`() = runTest {
        coEvery { hojaRutaRepository.obtenerRuta(99) } returns null

        assertTrue(cerrarRuta(rutaId = 99).isFailure)
        assertTrue(omitirEstacion(rutaId = 99, estacionId = 1).isFailure)
    }

    // ---------- Derivados del modelo ----------

    @Test
    fun `el avance cuenta completadas y omitidas como resueltas`() {
        val ruta = rutaCompleta.copy(
            estaciones = listOf(
                bancaEnRuta(1, EstadoVisitaEstacion.COMPLETADA),
                bancaEnRuta(2, EstadoVisitaEstacion.OMITIDA),
                bancaEnRuta(3, EstadoVisitaEstacion.PENDIENTE),
                bancaEnRuta(4, EstadoVisitaEstacion.PENDIENTE)
            )
        )

        assertEquals(4, ruta.cantidadEstaciones)
        assertEquals(2, ruta.estacionesCuadradas)
        assertEquals(2, ruta.estacionesPendientes)
        assertEquals(0.5f, ruta.porcentajeAvance, 0.001f)
    }

    @Test
    fun `una ruta sin bancas no divide entre cero`() {
        val vacia = rutaCompleta.copy(estaciones = emptyList())

        assertEquals(0f, vacia.porcentajeAvance, 0.0f)
    }

    @Test
    fun `PENDIENTE y EN_PROGRESO cuentan como abiertas`() {
        assertTrue(EstadoRuta.PENDIENTE.estaAbierta)
        assertTrue(EstadoRuta.EN_PROGRESO.estaAbierta)
        assertFalse(EstadoRuta.CERRADA.estaAbierta)
        assertEquals(listOf("PENDIENTE", "EN_PROGRESO"), EstadoRuta.NOMBRES_ABIERTOS)
    }
}
