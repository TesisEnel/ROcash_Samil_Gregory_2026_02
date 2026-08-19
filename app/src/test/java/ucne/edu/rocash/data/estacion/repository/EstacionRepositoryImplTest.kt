package ucne.edu.rocash.data.estacion.repository

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ucne.edu.rocash.data.estacion.local.EstacionVentasDao
import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

class EstacionRepositoryImplTest {
    private lateinit var dao: EstacionVentasDao
    private lateinit var repository: EstacionRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = EstacionRepositoryImpl(dao)
    }

    private fun estacionEntity(
        id: Int = 1,
        nombre: String = "Banca Central",
        agenteId2: Int? = null
    ) = EstacionVentasEntity(
        estacionId = id,
        nombre = nombre,
        direccion = "Calle Duarte $id",
        agenteId = 7,
        agenteId2 = agenteId2
    )

    @Test
    fun `getEstacion mapea la entity a dominio`() = runTest {
        coEvery { dao.getById(1) } returns estacionEntity()

        val estacion = repository.getEstacion(1)!!

        assertEquals(1, estacion.estacionId)
        assertEquals("Banca Central", estacion.nombre)
        assertEquals("Calle Duarte 1", estacion.direccion)
        assertEquals(7, estacion.agenteId)
        assertNull(estacion.agenteId2)
    }

    @Test
    fun `getEstacion conserva el segundo agente cuando la banca lo tiene`() = runTest {
        coEvery { dao.getById(1) } returns estacionEntity(agenteId2 = 12)

        assertEquals(12, repository.getEstacion(1)!!.agenteId2)
    }

    @Test
    fun `getEstacion devuelve null cuando no existe`() = runTest {
        coEvery { dao.getById(404) } returns null

        assertNull(repository.getEstacion(404))
    }

    @Test
    fun `upsert convierte a entity y devuelve el id`() = runTest {
        val estacion = EstacionVentas(
            estacionId = 4,
            nombre = "Banca Norte",
            direccion = "Av. Principal",
            agenteId = 2,
            agenteId2 = 3
        )

        val id = repository.upsert(estacion)

        assertEquals(4, id)
        coVerify(exactly = 1) {
            dao.upsert(
                match<EstacionVentasEntity> {
                    it.estacionId == 4 && it.nombre == "Banca Norte" && it.agenteId2 == 3
                }
            )
        }
    }

    @Test
    fun `exists refleja lo que responde el DAO`() = runTest {
        coEvery { dao.exists(1) } returns true
        coEvery { dao.exists(2) } returns false

        assertTrue(repository.exists(1))
        assertFalse(repository.exists(2))
    }

    @Test
    fun `delete borra por id`() = runTest {
        repository.delete(5)

        coVerify(exactly = 1) { dao.deleteById(5) }
    }

    @Test
    fun `observeEstaciones mapea toda la lista`() = runTest {
        every { dao.observeAll() } returns flowOf(
            listOf(estacionEntity(1, "Banca A"), estacionEntity(2, "Banca B"))
        )

        repository.observeEstaciones().test {
            val estaciones = awaitItem()

            assertEquals(2, estaciones.size)
            assertEquals(listOf("Banca A", "Banca B"), estaciones.map { it.nombre })
            awaitComplete()
        }
    }

    @Test
    fun `observeEstaciones emite lista vacia sin fallar`() = runTest {
        every { dao.observeAll() } returns flowOf(emptyList())

        repository.observeEstaciones().test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `buscarEstaciones pasa el termino tal cual al DAO`() = runTest {
        every { dao.search("Norte") } returns flowOf(listOf(estacionEntity(nombre = "Banca Norte")))

        repository.buscarEstaciones("Norte").test {
            assertEquals("Banca Norte", awaitItem().first().nombre)
            awaitComplete()
        }

        coVerify { dao.search("Norte") }
    }
}
