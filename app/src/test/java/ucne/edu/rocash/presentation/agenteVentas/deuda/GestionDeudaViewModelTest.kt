package ucne.edu.rocash.presentation.agenteVentas.deuda

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda
import ucne.edu.rocash.domain.abonoDeuda.usecase.ObservarAbonosDeAgenteUseCase
import ucne.edu.rocash.domain.abonoDeuda.usecase.ObservarTotalAbonadoUseCase
import ucne.edu.rocash.domain.abonoDeuda.usecase.RegistrarAbonoUseCase
import ucne.edu.rocash.domain.abonoDeuda.usecase.SaldarDeudaUseCase
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.usecase.ObservarAgenteUseCase
import ucne.edu.rocash.presentation.util.MainDispatcherRule

@ExperimentalCoroutinesApi
class GestionDeudaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var observarAgenteUseCase: ObservarAgenteUseCase
    private lateinit var observarAbonosUseCase: ObservarAbonosDeAgenteUseCase
    private lateinit var observarTotalAbonadoUseCase: ObservarTotalAbonadoUseCase
    private lateinit var registrarAbonoUseCase: RegistrarAbonoUseCase
    private lateinit var saldarDeudaUseCase: SaldarDeudaUseCase
    private lateinit var viewModel: GestionDeudaViewModel

    private val agente = MutableStateFlow<AgenteVentas?>(
        AgenteVentas(
            agenteId = 1,
            nombre = "Ramón Peralta",
            telefono = "8095551234",
            deudaAcumulada = 5000.0
        )
    )

    private val abonoDeEjemplo = AbonoDeuda(
        abonoId = 1,
        agenteId = 1,
        monto = 2000.0,
        deudaAntes = 5000.0,
        deudaDespues = 3000.0
    )

    @Before
    fun setup() {
        observarAgenteUseCase = mockk()
        observarAbonosUseCase = mockk()
        observarTotalAbonadoUseCase = mockk()
        registrarAbonoUseCase = mockk()
        saldarDeudaUseCase = mockk()

        every { observarAgenteUseCase(1) } returns agente
        every { observarAbonosUseCase(1) } returns flowOf(emptyList())
        every { observarTotalAbonadoUseCase(1) } returns flowOf(0.0)

        viewModel = GestionDeudaViewModel(
            observarAgenteUseCase,
            observarAbonosUseCase,
            observarTotalAbonadoUseCase,
            registrarAbonoUseCase,
            saldarDeudaUseCase
        )
    }

    private fun cargar() {
        viewModel.onEvent(GestionDeudaUiEvent.Load(1))
    }

    @Test
    fun `al cargar muestra la deuda del agente`() = runTest {
        cargar()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Ramón Peralta", state.nombreAgente)
        assertEquals(5000.0, state.deudaActual, 0.0)
        assertTrue(state.tieneDeuda)
    }

    @Test
    fun `el total abonado llega del dominio y no se suma en el ViewModel`() = runTest {
        every { observarAbonosUseCase(1) } returns flowOf(listOf(abonoDeEjemplo))
        // El use case devuelve 7500 a proposito, distinto de la suma de la
        // lista: si el ViewModel recorriera los abonos, este test fallaria.
        every { observarTotalAbonadoUseCase(1) } returns flowOf(7500.0)

        cargar()
        advanceUntilIdle()

        assertEquals(7500.0, viewModel.state.value.totalAbonado, 0.0)
        assertTrue(viewModel.state.value.hayAbonos)
    }

    @Test
    fun `no permite abonar sin monto`() = runTest {
        cargar()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.puedeAbonar)

        viewModel.onEvent(GestionDeudaUiEvent.MontoChanged("1000"))

        assertTrue(viewModel.state.value.puedeAbonar)
    }

    @Test
    fun `no permite abonar a un agente sin deuda`() = runTest {
        agente.value = agente.value?.copy(deudaAcumulada = 0.0)

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(GestionDeudaUiEvent.MontoChanged("1000"))

        assertFalse(viewModel.state.value.tieneDeuda)
        assertFalse(viewModel.state.value.puedeAbonar)
    }

    @Test
    fun `un abono exitoso limpia el formulario y avisa`() = runTest {
        coEvery { registrarAbonoUseCase(1, "2000", any()) } returns
                Result.success(abonoDeEjemplo)

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(GestionDeudaUiEvent.MontoChanged("2000"))
        viewModel.onEvent(GestionDeudaUiEvent.Abonar)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("", state.montoAbono)
        assertEquals("Abono registrado.", state.mensaje)
        assertFalse(state.isProcesando)
        assertNull(state.errorMessage)
    }

    @Test
    fun `un fallo de validacion cae en el campo y no en el snackbar`() = runTest {
        coEvery { registrarAbonoUseCase(1, "9000", any()) } returns
                Result.failure(IllegalArgumentException("El abono supera la deuda"))

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(GestionDeudaUiEvent.MontoChanged("9000"))
        viewModel.onEvent(GestionDeudaUiEvent.Abonar)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("El abono supera la deuda", state.montoError)
        assertNull(state.errorMessage)
        // El monto se conserva para que el usuario pueda corregirlo.
        assertEquals("9000", state.montoAbono)
    }

    @Test
    fun `un fallo de infraestructura cae en el snackbar y no en el campo`() = runTest {
        coEvery { registrarAbonoUseCase(1, "1000", any()) } returns
                Result.failure(IllegalStateException("La base de datos está bloqueada"))

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(GestionDeudaUiEvent.MontoChanged("1000"))
        viewModel.onEvent(GestionDeudaUiEvent.Abonar)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("La base de datos está bloqueada", state.errorMessage)
        assertNull(state.montoError)
    }

    @Test
    fun `saldar pide confirmacion antes de tocar la deuda`() = runTest {
        cargar()
        advanceUntilIdle()

        viewModel.onEvent(GestionDeudaUiEvent.PedirConfirmacionSaldar)

        assertTrue(viewModel.state.value.mostrarDialogoSaldar)
        coVerify(exactly = 0) { saldarDeudaUseCase(any(), any()) }
    }

    @Test
    fun `cancelar cierra el dialogo sin saldar`() = runTest {
        cargar()
        advanceUntilIdle()

        viewModel.onEvent(GestionDeudaUiEvent.PedirConfirmacionSaldar)
        viewModel.onEvent(GestionDeudaUiEvent.CancelarSaldar)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.mostrarDialogoSaldar)
        coVerify(exactly = 0) { saldarDeudaUseCase(any(), any()) }
    }

    @Test
    fun `confirmar saldar cierra el dialogo y avisa`() = runTest {
        coEvery { saldarDeudaUseCase(1, any()) } returns Result.success(
            abonoDeEjemplo.copy(monto = 5000.0, deudaDespues = 0.0)
        )

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(GestionDeudaUiEvent.PedirConfirmacionSaldar)
        viewModel.onEvent(GestionDeudaUiEvent.Saldar)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.mostrarDialogoSaldar)
        assertEquals("Deuda saldada.", state.mensaje)
        coVerify(exactly = 1) { saldarDeudaUseCase(1, any()) }
    }

    @Test
    fun `MensajeMostrado y ErrorMostrado apagan sus banderas`() = runTest {
        coEvery { registrarAbonoUseCase(1, "1000", any()) } returns
                Result.success(abonoDeEjemplo)

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(GestionDeudaUiEvent.MontoChanged("1000"))
        viewModel.onEvent(GestionDeudaUiEvent.Abonar)
        advanceUntilIdle()

        viewModel.onEvent(GestionDeudaUiEvent.MensajeMostrado)
        assertNull(viewModel.state.value.mensaje)

        viewModel.onEvent(GestionDeudaUiEvent.ErrorMostrado)
        assertNull(viewModel.state.value.errorMessage)
    }
}
