package ucne.edu.rocash.data.agenteVentas.repository

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
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasDao
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasEntity
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

class AgenteVentasRepositoryImplTest {

    private lateinit var dao: AgenteVentasDao
    private lateinit var repository: AgenteVentasRepositoryImpl

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = AgenteVentasRepositoryImpl(dao)
    }

    private fun agenteEntity(
        id: Int = 1,
        nombre: String = "Juan Pérez",
        telefono: String = "8095551234",
        deuda: Double = 0.0
    ) = AgenteVentasEntity(
        agenteId = id,
        nombre = nombre,
        telefono = telefono,
        deudaAcumulada = deuda,
        estado = true
    )

    // ---------- deuda: el bug que perdía dinero en silencio ----------

    @Test
    fun `sumarDeuda delega directamente en el UPDATE atomico del DAO`() = runTest {
        repository.sumarDeuda(agenteId = 3, monto = 500.0)

        coVerify(exactly = 1) { dao.sumarDeuda(3, 500.0) }
    }

    @Test
    fun `sumarDeuda admite montos negativos para corregir un cuadre anterior`() = runTest {
        repository.sumarDeuda(agenteId = 3, monto = -400.0)

        coVerify(exactly = 1) { dao.sumarDeuda(3, -400.0) }
    }

    @Test
    fun `sumarDeuda no lee el agente antes de escribir`() = runTest {
        // La version anterior hacia read-modify-write y podia perder la deuda
        // si el agente no pasaba las validaciones de nombre y telefono.
        repository.sumarDeuda(agenteId = 3, monto = 500.0)

        coVerify(exactly = 0) { dao.getById(any()) }
        coVerify(exactly = 0) { dao.upsert(any()) }
    }

    // ---------- CRUD ----------

    @Test
    fun `upsert convierte a entity y devuelve el id del agente`() = runTest {
        val agente = AgenteVentas(
            agenteId = 9,
            nombre = "Ana",
            telefono = "8299998888",
            deudaAcumulada = 250.0
        )

        val id = repository.upsert(agente)

        assertEquals(9, id)
        coVerify(exactly = 1) {
            dao.upsert(
                match<AgenteVentasEntity> {
                    it.agenteId == 9 && it.nombre == "Ana" && it.deudaAcumulada == 250.0
                }
            )
        }
    }

    @Test
    fun `getAgente mapea la entity a dominio`() = runTest {
        coEvery { dao.getById(1) } returns agenteEntity(deuda = 1_250.5)

        val agente = repository.getAgente(1)!!

        assertEquals(1, agente.agenteId)
        assertEquals("Juan Pérez", agente.nombre)
        assertEquals(1_250.5, agente.deudaAcumulada, 0.001)
        assertTrue(agente.estado)
    }

    @Test
    fun `getAgente devuelve null cuando no existe`() = runTest {
        coEvery { dao.getById(404) } returns null

        assertNull(repository.getAgente(404))
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
    fun `observeAgentes mapea toda la lista`() = runTest {
        every { dao.observeAll() } returns flowOf(
            listOf(agenteEntity(id = 1, nombre = "Ana"), agenteEntity(id = 2, nombre = "Luis"))
        )

        repository.observeAgentes().test {
            val agentes = awaitItem()

            assertEquals(2, agentes.size)
            assertEquals(listOf("Ana", "Luis"), agentes.map { it.nombre })
            awaitComplete()
        }
    }

    @Test
    fun `buscarAgentesPorNombre pasa el termino tal cual al DAO`() = runTest {
        every { dao.searchByName("Ana") } returns flowOf(listOf(agenteEntity(nombre = "Ana")))

        repository.buscarAgentesPorNombre("Ana").test {
            assertEquals("Ana", awaitItem().first().nombre)
            awaitComplete()
        }

        coVerify { dao.searchByName("Ana") }
    }
}