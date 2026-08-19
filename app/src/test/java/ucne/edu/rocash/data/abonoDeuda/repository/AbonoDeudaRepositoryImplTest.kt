package ucne.edu.rocash.data.abonoDeuda.repository

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
import ucne.edu.rocash.data.abonoDeuda.local.AbonoDeudaDao
import ucne.edu.rocash.data.abonoDeuda.local.AbonoDeudaEntity
import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda

class AbonoDeudaRepositoryImplTest {

    private lateinit var dao: AbonoDeudaDao
    private lateinit var repository: AbonoDeudaRepositoryImpl

    private val entity = AbonoDeudaEntity(
        abonoId = 1,
        agenteId = 7,
        monto = 2_000.0,
        deudaAntes = 5_000.0,
        deudaDespues = 3_000.0,
        fecha = 1_770_000_000_000,
        nota = "Entregó en la banca"
    )

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = AbonoDeudaRepositoryImpl(dao)
    }

    @Test
    fun `observarAbonosDeAgente mapea las entidades al dominio`() = runTest {
        every { dao.observarPorAgente(7) } returns flowOf(listOf(entity))

        repository.observarAbonosDeAgente(7).test {
            val lista = awaitItem()

            assertEquals(1, lista.size)
            with(lista.first()) {
                assertEquals(1, abonoId)
                assertEquals(7, agenteId)
                assertEquals(2_000.0, monto, 0.0)
                assertEquals(5_000.0, deudaAntes, 0.0)
                assertEquals(3_000.0, deudaDespues, 0.0)
                assertEquals(1_770_000_000_000, fecha)
                assertEquals("Entregó en la banca", nota)
            }
            awaitComplete()
        }
    }

    @Test
    fun `un agente sin abonos devuelve lista vacia`() = runTest {
        every { dao.observarPorAgente(7) } returns flowOf(emptyList())

        repository.observarAbonosDeAgente(7).test {
            assertEquals(emptyList<AbonoDeuda>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `un abono sin nota conserva el null`() = runTest {
        every { dao.observarPorAgente(7) } returns flowOf(listOf(entity.copy(nota = null)))

        repository.observarAbonosDeAgente(7).test {
            assertNull(awaitItem().first().nota)
            awaitComplete()
        }
    }

    @Test
    fun `observarTotalAbonado delega en el DAO`() = runTest {
        // La suma la hace SQLite: el repositorio no recorre la lista.
        every { dao.observarTotalAbonado(7) } returns flowOf(9_500.0)

        repository.observarTotalAbonado(7).test {
            assertEquals(9_500.0, awaitItem(), 0.0)
            awaitComplete()
        }
    }

    @Test
    fun `observarTotalAbonosGlobal delega en el DAO`() = runTest {
        every { dao.observarTotalAbonosGlobal() } returns flowOf(31_000.0)

        repository.observarTotalAbonosGlobal().test {
            assertEquals(31_000.0, awaitItem(), 0.0)
            awaitComplete()
        }
    }

    @Test
    fun `registrar convierte a entity y devuelve el id generado`() = runTest {
        coEvery { dao.insertar(any()) } returns 42L

        val id = repository.registrar(
            AbonoDeuda(
                agenteId = 7,
                monto = 2_000.0,
                deudaAntes = 5_000.0,
                deudaDespues = 3_000.0,
                fecha = 1_770_000_000_000
            )
        )

        assertEquals(42, id)
        coVerify(exactly = 1) {
            dao.insertar(
                match {
                    it.agenteId == 7 &&
                            it.monto == 2_000.0 &&
                            it.deudaAntes == 5_000.0 &&
                            it.deudaDespues == 3_000.0
                }
            )
        }
    }

    @Test
    fun `registrar deja que Room genere el id cuando llega en cero`() = runTest {
        coEvery { dao.insertar(any()) } returns 8L

        val id = repository.registrar(
            AbonoDeuda(agenteId = 7, monto = 100.0, deudaAntes = 100.0, deudaDespues = 0.0)
        )

        assertEquals(8, id)
        coVerify(exactly = 1) { dao.insertar(match { it.abonoId == 0 }) }
    }
}
