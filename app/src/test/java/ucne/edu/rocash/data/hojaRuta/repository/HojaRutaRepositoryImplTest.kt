package ucne.edu.rocash.data.hojaRuta.repository

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity
import ucne.edu.rocash.data.hojaRuta.local.EstacionDeRutaEntity
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaConEstaciones
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaDao
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaEntity
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaEstacionEntity
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

class HojaRutaRepositoryImplTest {

    private lateinit var dao: HojaRutaDao
    private lateinit var repository: HojaRutaRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = HojaRutaRepositoryImpl(dao)
    }

    // ---------- helpers ----------

    private fun rutaEntity(
        id: Int = 1,
        estado: String = "PENDIENTE",
        fechaCierre: Long? = null
    ) = HojaRutaEntity(
        id = id,
        recolectorId = "uid-1",
        fechaCreacion = 1_000L,
        fechaCierre = fechaCierre,
        estado = estado,
        totalVentaBruta = 0.0,
        totalComisionClientes = 0.0,
        totalRecaudado = 0.0,
        totalDeudas = 0.0
    )

    private fun cruce(
        estacionId: Int,
        orden: Int,
        estado: String = "PENDIENTE",
        rutaId: Int = 1
    ) = EstacionDeRutaEntity(
        cruce = HojaRutaEstacionEntity(rutaId, estacionId, orden, estado),
        estacion = EstacionVentasEntity(
            estacionId = estacionId,
            nombre = "Banca $estacionId",
            direccion = "Calle $estacionId",
            agenteId = 7,
            agenteId2 = null
        )
    )

    // ---------- creación ----------

    @Test
    fun `crearRutaConEstaciones delega en el DAO con estado inicial PENDIENTE`() = runTest {
        coEvery { dao.crearRutaConEstaciones(any(), any(), any()) } returns 42

        val nuevoId = repository.crearRutaConEstaciones(
            ruta = HojaRuta(recolectorId = "uid-1", estado = EstadoRuta.PENDIENTE),
            estacionIds = listOf(3, 1, 2)
        )

        assertEquals(42, nuevoId)
        coVerify(exactly = 1) {
            dao.crearRutaConEstaciones(
                ruta = any(),
                estacionIds = listOf(3, 1, 2),
                estadoInicialEstacion = "PENDIENTE"
            )
        }
    }

    // ---------- cierre ----------

    @Test
    fun `cerrarRuta envia el estado CERRADA y los totales recibidos`() = runTest {
        repository.cerrarRuta(
            rutaId = 5,
            fechaCierre = 9_999L,
            totalVentaBruta = 1_000.0,
            totalComisionClientes = 200.0,
            totalRecaudado = 750.0,
            totalDeudas = 50.0
        )

        coVerify(exactly = 1) {
            dao.cerrarConTotales(
                rutaId = 5,
                estadoCerrada = "CERRADA",
                fechaCierre = 9_999L,
                totalVentaBruta = 1_000.0,
                totalComisionClientes = 200.0,
                totalRecaudado = 750.0,
                totalDeudas = 50.0
            )
        }
    }

    @Test
    fun `marcarEstadoEstacion traduce el enum a su nombre`() = runTest {
        repository.marcarEstadoEstacion(1, 2, EstadoVisitaEstacion.OMITIDA)

        coVerify(exactly = 1) { dao.marcarEstadoEstacion(1, 2, "OMITIDA") }
    }

    @Test
    fun `cambiarEstadoRuta traduce el enum a su nombre`() = runTest {
        repository.cambiarEstadoRuta(1, EstadoRuta.EN_PROGRESO)

        coVerify(exactly = 1) { dao.cambiarEstado(1, "EN_PROGRESO") }
    }

    // ---------- lectura ----------

    @Test
    fun `observarRutasAbiertas consulta solo PENDIENTE y EN_PROGRESO`() = runTest {
        every { dao.observarRutasAbiertas(any(), any()) } returns flowOf(emptyList())

        repository.observarRutasAbiertas("uid-1").test {
            awaitItem()
            awaitComplete()
        }

        coVerify {
            dao.observarRutasAbiertas("uid-1", listOf("PENDIENTE", "EN_PROGRESO"))
        }
    }

    @Test
    fun `observarRuta ordena las estaciones por el campo orden`() = runTest {
        val relacion = HojaRutaConEstaciones(
            ruta = rutaEntity(estado = "EN_PROGRESO"),
            estaciones = listOf(
                cruce(estacionId = 30, orden = 2),
                cruce(estacionId = 10, orden = 0, estado = "COMPLETADA"),
                cruce(estacionId = 20, orden = 1)
            )
        )
        every { dao.observarRutaConEstaciones(1) } returns flowOf(relacion)

        repository.observarRuta(1).test {
            val ruta = awaitItem()!!

            assertEquals(EstadoRuta.EN_PROGRESO, ruta.estado)
            assertEquals(listOf(10, 20, 30), ruta.estaciones.map { it.estacionId })
            assertEquals(EstadoVisitaEstacion.COMPLETADA, ruta.estaciones.first().estado)
            awaitComplete()
        }
    }

    @Test
    fun `observarRuta emite null cuando la ruta no existe`() = runTest {
        every { dao.observarRutaConEstaciones(99) } returns flowOf(null)

        repository.observarRuta(99).test {
            assertNull(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `un estado invalido en la columna no revienta y cae en PENDIENTE`() = runTest {
        val relacion = HojaRutaConEstaciones(
            ruta = rutaEntity(estado = "BASURA_DE_UNA_VERSION_VIEJA"),
            estaciones = emptyList()
        )
        every { dao.observarRutaConEstaciones(1) } returns flowOf(relacion)

        repository.observarRuta(1).test {
            assertEquals(EstadoRuta.PENDIENTE, awaitItem()!!.estado)
            awaitComplete()
        }
    }

    @Test
    fun `observarHistorial consulta solo rutas CERRADA`() = runTest {
        every { dao.observarHistorial(any(), any()) } returns flowOf(
            listOf(rutaEntity(id = 7, estado = "CERRADA", fechaCierre = 5_000L))
        )

        repository.observarHistorial("uid-1").test {
            val rutas = awaitItem()

            assertEquals(1, rutas.size)
            assertEquals(EstadoRuta.CERRADA, rutas.first().estado)
            assertEquals(5_000L, rutas.first().fechaCierre)
            // El historial no arrastra estaciones: la consulta es sobre la cabecera.
            assertTrue(rutas.first().estaciones.isEmpty())
            awaitComplete()
        }

        coVerify { dao.observarHistorial("uid-1", "CERRADA") }
    }

    // ---------- estaciones comprometidas ----------

    @Test
    fun `estacionesYaComprometidas no toca el DAO cuando la lista viene vacia`() = runTest {
        val resultado = repository.estacionesYaComprometidas(emptyList())

        assertTrue(resultado.isEmpty())
        coVerify(exactly = 0) { dao.estacionesYaComprometidas(any(), any()) }
    }

    @Test
    fun `estacionesYaComprometidas consulta con los estados abiertos`() = runTest {
        coEvery { dao.estacionesYaComprometidas(any(), any()) } returns listOf(2, 5)

        val resultado = repository.estacionesYaComprometidas(listOf(1, 2, 5))

        assertEquals(listOf(2, 5), resultado)
        coVerify {
            dao.estacionesYaComprometidas(listOf(1, 2, 5), listOf("PENDIENTE", "EN_PROGRESO"))
        }
    }

    @Test
    fun `observarEstacionesComprometidas elimina duplicados al convertir a Set`() = runTest {
        every { dao.observarEstacionesComprometidas(any()) } returns flowOf(listOf(1, 2, 2, 3))

        repository.observarEstacionesComprometidas().test {
            assertEquals(setOf(1, 2, 3), awaitItem())
            awaitComplete()
        }
    }
}