package ucne.edu.rocash.domain.abonoDeuda

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda
import ucne.edu.rocash.domain.abonoDeuda.repository.AbonoDeudaRepository
import ucne.edu.rocash.domain.abonoDeuda.usecase.RegistrarAbonoUseCase
import ucne.edu.rocash.domain.abonoDeuda.usecase.SaldarDeudaUseCase
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository

class RegistrarAbonoUseCaseTest {

    private lateinit var abonoRepository: AbonoDeudaRepository
    private lateinit var agenteRepository: AgenteVentasRepository
    private lateinit var registrarAbono: RegistrarAbonoUseCase
    private lateinit var saldarDeuda: SaldarDeudaUseCase

    private val agenteConDeuda = AgenteVentas(
        agenteId = 1,
        nombre = "Ramón Peralta",
        telefono = "8095551234",
        deudaAcumulada = 5000.0
    )

    @Before
    fun setup() {
        abonoRepository = mockk(relaxed = true)
        agenteRepository = mockk(relaxed = true)
        registrarAbono = RegistrarAbonoUseCase(abonoRepository, agenteRepository)
        saldarDeuda = SaldarDeudaUseCase(abonoRepository, agenteRepository)

        coEvery { agenteRepository.getAgente(1) } returns agenteConDeuda
        coEvery { abonoRepository.registrar(any()) } returns 7
    }

    @Test
    fun `un abono valido descuenta la deuda y deja el registro`() = runTest {
        val resultado = registrarAbono(agenteId = 1, montoTexto = "2000")

        assertTrue(resultado.isSuccess)
        val abono = resultado.getOrThrow()
        assertEquals(2000.0, abono.monto, 0.0)
        assertEquals(5000.0, abono.deudaAntes, 0.0)
        assertEquals(3000.0, abono.deudaDespues, 0.0)

        // El descuento va como delta negativo: la resta ocurre dentro del UPDATE
        // de SQLite y no aquí, para que dos abonos simultaneos no se pisen.
        coVerify(exactly = 1) { agenteRepository.sumarDeuda(1, -2000.0) }
        coVerify(exactly = 1) { abonoRepository.registrar(any<AbonoDeuda>()) }
    }

    @Test
    fun `un abono mayor que la deuda se rechaza y no toca nada`() = runTest {
        val resultado = registrarAbono(agenteId = 1, montoTexto = "9000")

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { agenteRepository.sumarDeuda(any(), any()) }
        coVerify(exactly = 0) { abonoRepository.registrar(any()) }
    }

    @Test
    fun `un abono no numerico se rechaza`() = runTest {
        assertTrue(registrarAbono(agenteId = 1, montoTexto = "mil pesos").isFailure)
        coVerify(exactly = 0) { agenteRepository.sumarDeuda(any(), any()) }
    }

    @Test
    fun `un abono de cero o negativo se rechaza`() = runTest {
        assertTrue(registrarAbono(agenteId = 1, montoTexto = "0").isFailure)
        assertTrue(registrarAbono(agenteId = 1, montoTexto = "-500").isFailure)
        coVerify(exactly = 0) { agenteRepository.sumarDeuda(any(), any()) }
    }

    @Test
    fun `abonar a un agente sin deuda se rechaza`() = runTest {
        coEvery { agenteRepository.getAgente(2) } returns
                agenteConDeuda.copy(agenteId = 2, deudaAcumulada = 0.0)

        assertTrue(registrarAbono(agenteId = 2, montoTexto = "100").isFailure)
        coVerify(exactly = 0) { agenteRepository.sumarDeuda(any(), any()) }
    }

    @Test
    fun `saldar deja la deuda en cero y lo registra como abono por el total`() = runTest {
        val resultado = saldarDeuda(agenteId = 1)

        assertTrue(resultado.isSuccess)
        val abono = resultado.getOrThrow()
        assertEquals(5000.0, abono.monto, 0.0)
        assertEquals(0.0, abono.deudaDespues, 0.0)

        coVerify(exactly = 1) { agenteRepository.sumarDeuda(1, -5000.0) }
        coVerify(exactly = 1) { abonoRepository.registrar(any<AbonoDeuda>()) }
    }

    @Test
    fun `saldar a un agente sin deuda se rechaza`() = runTest {
        coEvery { agenteRepository.getAgente(3) } returns
                agenteConDeuda.copy(agenteId = 3, deudaAcumulada = 0.0)

        assertTrue(saldarDeuda(agenteId = 3).isFailure)
        coVerify(exactly = 0) { agenteRepository.sumarDeuda(any(), any()) }
    }
}
