package ucne.edu.rocash.presentation.hojaRuta.cuadre

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.usecase.GetEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.model.CalculoCuadre
import ucne.edu.rocash.domain.registroRecoleccion.usecase.CalcularCuadreUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ObtenerCuadreDeEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ProcesarRecoleccionUseCase
import ucne.edu.rocash.presentation.util.MainDispatcherRule

@ExperimentalCoroutinesApi
class CuadreViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var procesarRecoleccionUseCase: ProcesarRecoleccionUseCase
    private lateinit var obtenerCuadreDeEstacionUseCase: ObtenerCuadreDeEstacionUseCase
    private lateinit var calcularCuadreUseCase: CalcularCuadreUseCase
    private lateinit var getEstacionUseCase: GetEstacionUseCase
    private lateinit var viewModel: CuadreViewModel

    private val estacion = EstacionVentas(
        estacionId = 10,
        nombre = "Banca Norte",
        direccion = "Av. Principal 12",
        agenteId = 1
    )

    @Before
    fun setup() {
        procesarRecoleccionUseCase = mockk()
        obtenerCuadreDeEstacionUseCase = mockk()
        getEstacionUseCase = mockk()

        // El cálculo es puro: se usa el real para verificar que el ViewModel
        // muestra exactamente lo que el dominio produce, sin reimplementarlo.
        calcularCuadreUseCase = CalcularCuadreUseCase()

        coEvery { getEstacionUseCase(any()) } returns estacion
        coEvery { obtenerCuadreDeEstacionUseCase(any(), any()) } returns null

        viewModel = CuadreViewModel(
            procesarRecoleccionUseCase,
            obtenerCuadreDeEstacionUseCase,
            calcularCuadreUseCase,
            getEstacionUseCase
        )
    }

    private fun cargarFormulario() {
        viewModel.onEvent(CuadreUiEvent.Load(1, 10, 1, "Banca Norte"))
    }

    private fun llenarMontos(
        ventaBruta: String = "5000",
        comision: String = "1000",
        recolectado: String = "4000"
    ) {
        viewModel.onEvent(CuadreUiEvent.VentaBrutaChanged(ventaBruta))
        viewModel.onEvent(CuadreUiEvent.ComisionChanged(comision))
        viewModel.onEvent(CuadreUiEvent.MontoRecolectadoChanged(recolectado))
    }

    @Test
    fun `no permite guardar mientras falte algun monto`() = runTest {
        cargarFormulario()
        advanceUntilIdle()

        llenarMontos(recolectado = "")

        assertFalse(viewModel.state.value.puedeGuardar)
    }

    @Test
    fun `permite guardar con los tres montos presentes`() = runTest {
        cargarFormulario()
        advanceUntilIdle()

        llenarMontos()

        assertTrue(viewModel.state.value.puedeGuardar)
    }

    @Test
    fun `los montos mostrados son los que calcula el dominio`() = runTest {
        cargarFormulario()
        advanceUntilIdle()

        llenarMontos(ventaBruta = "5000", comision = "1000", recolectado = "3500")

        val esperado = CalculoCuadre.desdeTexto("5000", "1000", "3500")
        val state = viewModel.state.value

        assertEquals(esperado.montoEsperado, state.montoEsperado, 0.0)
        assertEquals(esperado.montoDeuda, state.deudaGenerada, 0.0)
        assertTrue(state.hayDeuda)
    }

    @Test
    fun `sin deuda no enciende la bandera de deuda`() = runTest {
        cargarFormulario()
        advanceUntilIdle()

        llenarMontos(ventaBruta = "5000", comision = "1000", recolectado = "4000")

        val state = viewModel.state.value
        assertEquals(0.0, state.deudaGenerada, 0.0)
        assertFalse(state.hayDeuda)
        assertFalse(state.deudaSeReparte)
    }

    @Test
    fun `la deuda se reparte solo si la banca tiene segundo agente`() = runTest {
        coEvery { getEstacionUseCase(any()) } returns estacion.copy(agenteId2 = 4)

        cargarFormulario()
        advanceUntilIdle()

        llenarMontos(ventaBruta = "10000", comision = "2000", recolectado = "5000")

        assertTrue(viewModel.state.value.deudaSeReparte)
    }

    @Test
    fun `guardar con montos invalidos no llama al caso de uso y marca los errores`() = runTest {
        cargarFormulario()
        advanceUntilIdle()

        llenarMontos(ventaBruta = "abc", comision = "1000", recolectado = "4000")
        viewModel.onEvent(CuadreUiEvent.Save)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("El valor debe ser numérico", state.ventaBrutaError)
        assertFalse(state.saved)
        assertFalse(state.isSaving)
    }

    @Test
    fun `editar un campo limpia solo su propio error`() = runTest {
        cargarFormulario()
        advanceUntilIdle()

        llenarMontos(ventaBruta = "abc", comision = "xyz", recolectado = "4000")
        viewModel.onEvent(CuadreUiEvent.Save)
        advanceUntilIdle()

        viewModel.onEvent(CuadreUiEvent.VentaBrutaChanged("5000"))

        val state = viewModel.state.value
        assertNull(state.ventaBrutaError)
        assertEquals("El valor debe ser numérico", state.comisionError)
    }

    @Test
    fun `guardar con exito enciende la bandera de navegacion`() = runTest {
        coEvery { procesarRecoleccionUseCase(any(), any(), any(), any(), any(), any(), any(), any()) } returns
                Result.success(1)

        cargarFormulario()
        advanceUntilIdle()

        llenarMontos()
        viewModel.onEvent(CuadreUiEvent.Save)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.saved)
        assertFalse(state.isSaving)
        assertNull(state.errorMessage)
    }

    @Test
    fun `guardar con fallo deja el mensaje y no navega`() = runTest {
        coEvery { procesarRecoleccionUseCase(any(), any(), any(), any(), any(), any(), any(), any()) } returns
                Result.failure(IllegalStateException("La ruta ya está cerrada"))

        cargarFormulario()
        advanceUntilIdle()

        llenarMontos()
        viewModel.onEvent(CuadreUiEvent.Save)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.saved)
        assertFalse(state.isSaving)
        assertEquals("La ruta ya está cerrada", state.errorMessage)
    }

    @Test
    fun `ErrorMostrado apaga el mensaje`() = runTest {
        coEvery { procesarRecoleccionUseCase(any(), any(), any(), any(), any(), any(), any(), any()) } returns
                Result.failure(IllegalStateException("boom"))

        cargarFormulario()
        advanceUntilIdle()

        llenarMontos()
        viewModel.onEvent(CuadreUiEvent.Save)
        advanceUntilIdle()

        viewModel.onEvent(CuadreUiEvent.ErrorMostrado)

        assertNull(viewModel.state.value.errorMessage)
    }
}
