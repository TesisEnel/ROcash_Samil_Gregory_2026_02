package ucne.edu.rocash.presentation.hojaRuta.crear

import io.mockk.coEvery
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
import ucne.edu.rocash.domain.auth.session.SesionRecolector
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.usecase.ObserveEstacionesUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.CrearHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.ObserveEstacionesComprometidasUseCase
import ucne.edu.rocash.presentation.util.MainDispatcherRule

@ExperimentalCoroutinesApi
class CrearRutaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var observeEstacionesUseCase: ObserveEstacionesUseCase
    private lateinit var observeEstacionesComprometidasUseCase: ObserveEstacionesComprometidasUseCase
    private lateinit var crearHojaRutaUseCase: CrearHojaRutaUseCase
    private lateinit var sesion: SesionRecolector

    private val disponibles = listOf(
        EstacionVentas(1, "Banca Norte", "Av. Principal 12", agenteId = 1),
        EstacionVentas(2, "Banca Sur", "Calle 8 esq. Duarte", agenteId = 2),
        EstacionVentas(3, "Banca Central", "Parque Duarte", agenteId = 1)
    )

    /** Mutable para poder simular que una banca pasa a estar comprometida. */
    private lateinit var comprometidas: MutableStateFlow<Set<Int>>

    @Before
    fun setup() {
        observeEstacionesUseCase = mockk()
        observeEstacionesComprometidasUseCase = mockk()
        crearHojaRutaUseCase = mockk()
        sesion = mockk()

        comprometidas = MutableStateFlow(setOf(3))

        every { observeEstacionesUseCase() } returns flowOf(disponibles)
        every { observeEstacionesComprometidasUseCase() } returns comprometidas
        every { sesion.recolectorIdOrNull() } returns "uid"
    }

    private fun crearViewModel() = CrearRutaViewModel(
        observeEstacionesUseCase,
        observeEstacionesComprometidasUseCase,
        crearHojaRutaUseCase,
        sesion
    )

    @Test
    fun `proyecta una fila por estacion con sus banderas resueltas`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(CrearRutaUiEvent.ToggleEstacion(1))

        val estaciones = viewModel.state.value.estaciones
        assertEquals(3, estaciones.size)
        assertTrue(estaciones.first { it.estacion.estacionId == 1 }.seleccionada)
        assertFalse(estaciones.first { it.estacion.estacionId == 2 }.seleccionada)
        assertTrue(estaciones.first { it.estacion.estacionId == 3 }.comprometida)
    }

    @Test
    fun `alternar dos veces deja la estacion sin seleccionar`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(CrearRutaUiEvent.ToggleEstacion(1))
        viewModel.onEvent(CrearRutaUiEvent.ToggleEstacion(1))

        val state = viewModel.state.value
        assertEquals(0, state.cantidadSeleccionada)
        assertFalse(state.haySeleccion)
        assertFalse(state.puedeGuardar)
    }

    @Test
    fun `una estacion comprometida no se puede seleccionar`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(CrearRutaUiEvent.ToggleEstacion(3))

        assertEquals(0, viewModel.state.value.cantidadSeleccionada)
    }

    @Test
    fun `una estacion que pasa a comprometida se retira de la seleccion`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(CrearRutaUiEvent.ToggleEstacion(2))
        assertEquals(1, viewModel.state.value.cantidadSeleccionada)

        comprometidas.value = setOf(2, 3)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(0, state.cantidadSeleccionada)
        assertFalse(state.puedeGuardar)
    }

    @Test
    fun `limpiar seleccion vacia los contadores`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(CrearRutaUiEvent.ToggleEstacion(1))
        viewModel.onEvent(CrearRutaUiEvent.LimpiarSeleccion)

        val state = viewModel.state.value
        assertEquals(0, state.cantidadSeleccionada)
        assertTrue(state.hayEstaciones)
    }

    @Test
    fun `sin estaciones marca la lista como vacia`() = runTest {
        every { observeEstacionesUseCase() } returns flowOf(emptyList())

        val viewModel = crearViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.hayEstaciones)
        assertFalse(state.isLoading)
    }

    @Test
    fun `crear la ruta enciende la bandera de navegacion con su id`() = runTest {
        coEvery { crearHojaRutaUseCase(any(), any()) } returns Result.success(42)

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(CrearRutaUiEvent.ToggleEstacion(1))
        viewModel.onEvent(CrearRutaUiEvent.GenerarHojaRuta)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(42, state.rutaCreadaId)
        assertFalse(state.isSaving)
    }

    @Test
    fun `un fallo al crear deja el mensaje y no navega`() = runTest {
        coEvery { crearHojaRutaUseCase(any(), any()) } returns
                Result.failure(IllegalStateException("Una banca ya está en otra ruta"))

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(CrearRutaUiEvent.ToggleEstacion(1))
        viewModel.onEvent(CrearRutaUiEvent.GenerarHojaRuta)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.rutaCreadaId)
        assertEquals("Una banca ya está en otra ruta", state.errorMessage)
    }

    @Test
    fun `ErrorMostrado apaga el mensaje`() = runTest {
        coEvery { crearHojaRutaUseCase(any(), any()) } returns
                Result.failure(IllegalStateException("boom"))

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(CrearRutaUiEvent.ToggleEstacion(1))
        viewModel.onEvent(CrearRutaUiEvent.GenerarHojaRuta)
        advanceUntilIdle()

        viewModel.onEvent(CrearRutaUiEvent.ErrorMostrado)

        assertNull(viewModel.state.value.errorMessage)
    }
}
