package ucne.edu.rocash.presentation.hojaRuta.historial

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
import ucne.edu.rocash.domain.auth.session.SesionRecolector
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.usecase.GetHistorialRutasUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.GetTotalIngresosUseCase
import ucne.edu.rocash.presentation.util.MainDispatcherRule

@ExperimentalCoroutinesApi
class HistorialRutasViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getHistorialRutasUseCase: GetHistorialRutasUseCase
    private lateinit var getTotalIngresosUseCase: GetTotalIngresosUseCase
    private lateinit var sesion: SesionRecolector

    private val rutaCerrada = HojaRuta(
        id = 7,
        recolectorId = "uid-cobrador",
        estado = EstadoRuta.CERRADA,
        totalRecaudado = 14_000.0
    )

    @Before
    fun setup() {
        getHistorialRutasUseCase = mockk()
        getTotalIngresosUseCase = mockk()
        sesion = mockk()

        every { sesion.recolectorIdOrNull() } returns "uid-cobrador"
        every { getHistorialRutasUseCase("uid-cobrador") } returns flowOf(listOf(rutaCerrada))
        every { getTotalIngresosUseCase("uid-cobrador") } returns flowOf(14_000.0)
    }

    private fun crearViewModel() = HistorialRutasViewModel(
        getHistorialRutasUseCase,
        getTotalIngresosUseCase,
        sesion
    )

    @Test
    fun `carga el historial y sus derivados`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(1, state.rutas.size)
        assertEquals(1, state.cantidadRutas)
        assertTrue(state.hayRutas)
        assertFalse(state.sinSesion)
    }

    @Test
    fun `el total historico llega del dominio y no se suma aqui`() = runTest {
        // El use case devuelve 99000 a propósito, distinto de la suma de la
        // lista: si el ViewModel recorriera las rutas, este test fallaría.
        // Es la corrección que se le hizo a totalRecaudadoHistorico, que antes
        // era un sumOf dentro del UiState.
        every { getTotalIngresosUseCase("uid-cobrador") } returns flowOf(99_000.0)

        val viewModel = crearViewModel()
        advanceUntilIdle()

        assertEquals(99_000.0, viewModel.state.value.totalRecaudadoHistorico, 0.0)
    }

    @Test
    fun `un historial vacio se marca como tal`() = runTest {
        every { getHistorialRutasUseCase("uid-cobrador") } returns flowOf(emptyList())
        every { getTotalIngresosUseCase("uid-cobrador") } returns flowOf(0.0)

        val viewModel = crearViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.hayRutas)
        assertEquals(0, state.cantidadRutas)
        assertFalse(state.isLoading)
    }

    @Test
    fun `sin sesion no consulta el historial`() = runTest {
        every { sesion.recolectorIdOrNull() } returns null

        val viewModel = crearViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.sinSesion)
        assertFalse(state.isLoading)
        io.mockk.verify(exactly = 0) { getHistorialRutasUseCase(any()) }
    }

    @Test
    fun `un fallo al cargar deja el mensaje y apaga el spinner`() = runTest {
        every { getHistorialRutasUseCase("uid-cobrador") } returns
                flow { throw IllegalStateException("Base de datos no disponible") }

        val viewModel = crearViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Base de datos no disponible", state.errorMessage)
    }

    @Test
    fun `ErrorMostrado apaga el mensaje`() = runTest {
        every { getHistorialRutasUseCase("uid-cobrador") } returns
                flow { throw IllegalStateException("boom") }

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(HistorialRutasUiEvent.ErrorMostrado)

        assertNull(viewModel.state.value.errorMessage)
    }
}
