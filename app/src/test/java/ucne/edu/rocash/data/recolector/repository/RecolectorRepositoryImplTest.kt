package ucne.edu.rocash.data.recolector.repository

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
import ucne.edu.rocash.data.recolector.local.RecolectorDao
import ucne.edu.rocash.data.recolector.local.RecolectorEntity
import ucne.edu.rocash.domain.recolector.model.Recolector

class RecolectorRepositoryImplTest {

    private lateinit var dao: RecolectorDao
    private lateinit var repository: RecolectorRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = RecolectorRepositoryImpl(dao)
    }

    private fun recolectorEntity(
        id: String = "uid-firebase-1",
        nombre: String = "Pedro Martínez",
        estado: Boolean = true
    ) = RecolectorEntity(
        id = id,
        nombre = nombre,
        telefono = "8095551234",
        cedula = "402-1234567-8",
        estado = estado
    )

    @Test
    fun `insertarRecolector convierte a entity antes de guardar`() = runTest {
        val recolector = Recolector(
            id = "uid-firebase-1",
            nombre = "Pedro Martínez",
            telefono = "8095551234",
            cedula = "402-1234567-8",
            estado = true
        )

        repository.insertarRecolector(recolector)

        coVerify(exactly = 1) {
            dao.upsert(
                match<RecolectorEntity> {
                    it.id == "uid-firebase-1" && it.cedula == "402-1234567-8" && it.estado
                }
            )
        }
    }

    @Test
    fun `el id se conserva como String porque viene del uid de Firebase`() = runTest {
        coEvery { dao.getById("uid-firebase-1") } returns recolectorEntity()

        val recolector = repository.obtenerRecolectorPorId("uid-firebase-1")!!

        assertEquals("uid-firebase-1", recolector.id)
        assertEquals("Pedro Martínez", recolector.nombre)
    }

    @Test
    fun `obtenerRecolectorPorId devuelve null cuando no existe`() = runTest {
        coEvery { dao.getById("desconocido") } returns null

        assertNull(repository.obtenerRecolectorPorId("desconocido"))
    }

    @Test
    fun `obtenerRecolectores mapea toda la lista`() = runTest {
        every { dao.observeAll() } returns flowOf(
            listOf(
                recolectorEntity(id = "uid-1", nombre = "Pedro"),
                recolectorEntity(id = "uid-2", nombre = "María", estado = false)
            )
        )

        repository.obtenerRecolectores().test {
            val recolectores = awaitItem()

            assertEquals(2, recolectores.size)
            assertEquals(listOf("Pedro", "María"), recolectores.map { it.nombre })
            assertTrue(recolectores.first().estado)
            awaitComplete()
        }
    }

    @Test
    fun `obtenerRecolectores emite lista vacia sin fallar`() = runTest {
        every { dao.observeAll() } returns flowOf(emptyList())

        repository.obtenerRecolectores().test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `buscarRecolectoresPorNombre pasa el termino tal cual al DAO`() = runTest {
        every { dao.searchByName("Pedro") } returns flowOf(listOf(recolectorEntity()))

        repository.buscarRecolectoresPorNombre("Pedro").test {
            assertEquals("Pedro Martínez", awaitItem().first().nombre)
            awaitComplete()
        }

        coVerify { dao.searchByName("Pedro") }
    }
}