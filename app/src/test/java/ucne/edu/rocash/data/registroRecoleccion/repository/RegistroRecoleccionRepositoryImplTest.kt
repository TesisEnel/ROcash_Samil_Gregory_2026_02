package ucne.edu.rocash.data.registroRecoleccion.repository

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import ucne.edu.rocash.data.registroRecoleccion.local.RegistroRecoleccionDao
import ucne.edu.rocash.data.registroRecoleccion.local.RegistroRecoleccionEntity
import ucne.edu.rocash.data.registroRecoleccion.local.ResumenRecoleccionRutaEntity
import ucne.edu.rocash.domain.registroRecoleccion.model.EstadoVisita
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion

class RegistroRecoleccionRepositoryImplTest {
    private lateinit var dao: RegistroRecoleccionDao
    private lateinit var repository: RegistroRecoleccionRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = RegistroRecoleccionRepositoryImpl(dao)
    }

    private fun registro(id: Int = 0, deuda: Double = 0.0) = RegistroRecoleccion(
        recoleccionId = id,
        hojaRutaId = 1,
        estacionId = 10,
        ventaBruta = 5_000.0,
        comisionCliente = 1_000.0,
        montoEsperado = 4_000.0,
        montoRecolectado = 4_000.0 - deuda,
        montoDeuda = deuda,
        estadoVisita = EstadoVisita.COMPLETADA,
        notaIncidencia = null
    )

    private fun registroEntity(id: Int = 3) = RegistroRecoleccionEntity(
        recoleccionId = id,
        hojaRutaId = 1,
        estacionId = 10,
        ventaBruta = 5_000.0,
        comisionCliente = 1_000.0,
        montoEsperado = 4_000.0,
        montoRecolectado = 3_500.0,
        montoDeuda = 500.0,
        estadoVisita = "COMPLETADA",
        notaIncidencia = "Cliente pidió plazo"
    )

    @Test
    fun `upsert de un registro nuevo devuelve el id que genera Room`() = runTest {
        coEvery { dao.upsert(any()) } returns 17L

        val id = repository.upsert(registro(id = 0))

        assertEquals(17, id)
    }

    @Test
    fun `upsert de un registro existente conserva su id cuando Room devuelve -1`() = runTest {
        coEvery { dao.upsert(any()) } returns -1L

        val id = repository.upsert(registro(id = 8))

        assertEquals(8, id)
    }

    @Test
    fun `upsert convierte el modelo de dominio a entity antes de guardarlo`() = runTest {
        coEvery { dao.upsert(any()) } returns 1L

        repository.upsert(registro(id = 4, deuda = 500.0))

        coVerify(exactly = 1) {
            dao.upsert(
                match<RegistroRecoleccionEntity> {
                    it.recoleccionId == 4 &&
                            it.montoDeuda == 500.0 &&
                            it.estadoVisita == "COMPLETADA"
                }
            )
        }
    }

    @Test
    fun `obtenerPorRutaYEstacion mapea la entity a dominio`() = runTest {
        coEvery { dao.getPorRutaYEstacion(1, 10) } returns registroEntity(id = 3)

        val previo = repository.obtenerPorRutaYEstacion(1, 10)!!

        assertEquals(3, previo.recoleccionId)
        assertEquals(500.0, previo.montoDeuda, 0.001)
        assertEquals(EstadoVisita.COMPLETADA, previo.estadoVisita)
        assertEquals("Cliente pidió plazo", previo.notaIncidencia)
    }

    @Test
    fun `obtenerPorRutaYEstacion devuelve null cuando la banca no se ha cuadrado`() = runTest {
        coEvery { dao.getPorRutaYEstacion(1, 99) } returns null

        assertNull(repository.obtenerPorRutaYEstacion(1, 99))
    }

    @Test
    fun `un estadoVisita invalido en la columna cae en COMPLETADA sin lanzar`() = runTest {
        coEvery { dao.getPorRutaYEstacion(1, 10) } returns
                registroEntity().copy(estadoVisita = "VALOR_VIEJO")

        val previo = repository.obtenerPorRutaYEstacion(1, 10)!!

        assertEquals(EstadoVisita.COMPLETADA, previo.estadoVisita)
    }

    @Test
    fun `obtenerResumenDeRuta mapea los agregados a dominio`() = runTest {
        coEvery { dao.obtenerResumenDeRuta(1) } returns ResumenRecoleccionRutaEntity(
            totalVentaBruta = 62_000.0,
            totalComisionClientes = 12_400.0,
            totalRecaudado = 47_600.0,
            totalDeudas = 2_000.0,
            cantidadRegistros = 4
        )

        val resumen = repository.obtenerResumenDeRuta(1)

        assertEquals(62_000.0, resumen.totalVentaBruta, 0.001)
        assertEquals(12_400.0, resumen.totalComisionClientes, 0.001)
        assertEquals(47_600.0, resumen.totalRecaudado, 0.001)
        assertEquals(2_000.0, resumen.totalDeudas, 0.001)
        assertEquals(4, resumen.cantidadRegistros)
    }

    @Test
    fun `una ruta sin cuadres devuelve un resumen en ceros`() = runTest {
        coEvery { dao.obtenerResumenDeRuta(1) } returns ResumenRecoleccionRutaEntity(
            totalVentaBruta = 0.0,
            totalComisionClientes = 0.0,
            totalRecaudado = 0.0,
            totalDeudas = 0.0,
            cantidadRegistros = 0
        )

        val resumen = repository.obtenerResumenDeRuta(1)

        assertEquals(0.0, resumen.totalRecaudado, 0.001)
        assertEquals(0, resumen.cantidadRegistros)
    }

    @Test
    fun `observarResumenDeRuta emite el resumen mapeado`() = runTest {
        every { dao.observarResumenDeRuta(1) } returns flowOf(
            ResumenRecoleccionRutaEntity(100.0, 20.0, 80.0, 0.0, 1)
        )

        repository.observarResumenDeRuta(1).test {
            val resumen = awaitItem()

            assertEquals(80.0, resumen.totalRecaudado, 0.001)
            assertEquals(1, resumen.cantidadRegistros)
            awaitComplete()
        }
    }

    @Test
    fun `observarPorRuta mapea cada entity de la lista`() = runTest {
        every { dao.observePorRuta(1) } returns flowOf(
            listOf(registroEntity(id = 1), registroEntity(id = 2))
        )

        repository.observarPorRuta(1).test {
            val registros = awaitItem()

            assertEquals(listOf(1, 2), registros.map { it.recoleccionId })
            awaitComplete()
        }
    }
}
