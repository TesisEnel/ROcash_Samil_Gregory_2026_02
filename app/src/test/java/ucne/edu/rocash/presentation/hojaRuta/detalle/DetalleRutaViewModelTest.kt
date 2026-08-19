package ucne.edu.rocash.presentation.hojaRuta.detalle

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
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.hojaRuta.model.EstacionEnRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.usecase.CerrarHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.ObserveHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.OmitirEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ObservarResumenDeRutaUseCase
import ucne.edu.rocash.presentation.util.MainDispatcherRule

@ExperimentalCoroutinesApi
class DetalleRutaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var observeHojaRutaUseCase: ObserveHojaRutaUseCase
    private lateinit var observarResumenUseCase: ObservarResumenDeRutaUseCase
    private lateinit var cerrarHojaRutaUseCase: CerrarHojaRutaUseCase
    private lateinit var omitirEstacionUseCase: OmitirEstacionUseCase
    private lateinit var viewModel: DetalleRutaViewModel

    private fun bancaEnRuta(id: Int, estado: EstadoVisitaEstacion) = EstacionEnRuta(
        estacion = EstacionVentas(id, "Banca $id", "Calle $id", agenteId = 1),
        estado = estado
    )

    private val rutaConPendientes = HojaRuta(
        id = 7,
        recolectorId = "uid-cobrador",
        estado = EstadoRuta.EN_PROGRESO,
        estaciones = listOf(
            bancaEnRuta(1, EstadoVisitaEstacion.COMPLETADA),
            bancaEnRuta(2, EstadoVisitaEstacion.PENDIENTE)
        )
    )

    private val ruta = MutableStateFlow<HojaRuta?>(rutaConPendientes)

    @Before
    fun setup() {
        observeHojaRutaUseCase = mockk()
        observarResumenUseCase = mockk()
        cerrarHojaRutaUseCase = mockk()
        omitirEstacionUseCase = mockk(relaxed = true)

        every { observeHojaRutaUseCase(7) } returns ruta
        every { observarResumenUseCase(7) } returns flowOf(
            ResumenRecoleccionRuta(totalRecaudado = 8_000.0, cantidadRegistros = 1)
        )
        coEvery { cerrarHojaRutaUseCase(7) } returns
                Result.success(rutaConPendientes.copy(estado = EstadoRuta.CERRADA))

        viewModel = DetalleRutaViewModel(
            observeHojaRutaUseCase,
            observarResumenUseCase,
            cerrarHojaRutaUseCase,
            omitirEstacionUseCase
        )
    }

    private fun cargar() = viewModel.onEvent(DetalleRutaUiEvent.Load(7))

    @Test
    fun `al cargar resuelve los derivados de la ruta`() = runTest {
        cargar()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(1, state.estacionesPendientes)
        assertTrue(state.hayEstacionesPendientes)
        assertFalse(state.rutaEstaCerrada)
        assertTrue(state.mostrarAccionCierre)
    }

    @Test
    fun `con bancas pendientes la ruta no puede cerrarse`() = runTest {
        cargar()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.puedeCerrarse)
    }

    @Test
    fun `sin bancas pendientes la ruta puede cerrarse`() = runTest {
        ruta.value = rutaConPendientes.copy(
            estaciones = listOf(
                bancaEnRuta(1, EstadoVisitaEstacion.COMPLETADA),
                bancaEnRuta(2, EstadoVisitaEstacion.OMITIDA)
            )
        )

        cargar()
        advanceUntilIdle()

        assertEquals(0, viewModel.state.value.estacionesPendientes)
        assertTrue(viewModel.state.value.puedeCerrarse)
    }

    @Test
    fun `una ruta ya cerrada oculta la accion de cierre`() = runTest {
        ruta.value = rutaConPendientes.copy(
            estado = EstadoRuta.CERRADA,
            estaciones = listOf(bancaEnRuta(1, EstadoVisitaEstacion.COMPLETADA))
        )

        cargar()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.rutaEstaCerrada)
        assertFalse(state.mostrarAccionCierre)
        assertFalse(state.puedeCerrarse)
    }

    @Test
    fun `una ruta inexistente se marca como no encontrada`() = runTest {
        ruta.value = null

        cargar()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.noEncontrada)
        assertFalse(viewModel.state.value.mostrarAccionCierre)
    }

    @Test
    fun `cerrar pide confirmacion antes de tocar la ruta`() = runTest {
        cargar()
        advanceUntilIdle()

        viewModel.onEvent(DetalleRutaUiEvent.PedirConfirmacionCierre)

        assertTrue(viewModel.state.value.mostrarDialogoCierre)
        coVerify(exactly = 0) { cerrarHojaRutaUseCase(any()) }
    }

    @Test
    fun `cancelar cierra el dialogo sin cerrar la ruta`() = runTest {
        cargar()
        advanceUntilIdle()

        viewModel.onEvent(DetalleRutaUiEvent.PedirConfirmacionCierre)
        viewModel.onEvent(DetalleRutaUiEvent.CancelarCierre)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.mostrarDialogoCierre)
        coVerify(exactly = 0) { cerrarHojaRutaUseCase(any()) }
    }

    @Test
    fun `confirmar cierra la ruta y enciende la navegacion`() = runTest {
        cargar()
        advanceUntilIdle()

        viewModel.onEvent(DetalleRutaUiEvent.ConfirmarCierre)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.cierreCompletado)
        assertFalse(state.isCerrando)
        assertFalse(state.mostrarDialogoCierre)
        coVerify(exactly = 1) { cerrarHojaRutaUseCase(7) }
    }

    @Test
    fun `un cierre fallido muestra el mensaje y no navega`() = runTest {
        coEvery { cerrarHojaRutaUseCase(7) } returns
                Result.failure(IllegalStateException("Faltan 1 estaciones por cuadrar: Banca 2"))

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(DetalleRutaUiEvent.ConfirmarCierre)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.cierreCompletado)
        assertFalse(state.isCerrando)
        assertEquals("Faltan 1 estaciones por cuadrar: Banca 2", state.errorMessage)
    }

    @Test
    fun `omitir una banca delega en el caso de uso`() = runTest {
        coEvery { omitirEstacionUseCase(7, 2) } returns Result.success(Unit)

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(DetalleRutaUiEvent.OmitirEstacion(2))
        advanceUntilIdle()

        coVerify(exactly = 1) { omitirEstacionUseCase(7, 2) }
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `un fallo al omitir se muestra`() = runTest {
        coEvery { omitirEstacionUseCase(7, 2) } returns
                Result.failure(IllegalStateException("La ruta ya está cerrada"))

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(DetalleRutaUiEvent.OmitirEstacion(2))
        advanceUntilIdle()

        assertEquals("La ruta ya está cerrada", viewModel.state.value.errorMessage)
    }

    @Test
    fun `ErrorMostrado apaga el mensaje`() = runTest {
        coEvery { omitirEstacionUseCase(7, 2) } returns
                Result.failure(IllegalStateException("boom"))

        cargar()
        advanceUntilIdle()

        viewModel.onEvent(DetalleRutaUiEvent.OmitirEstacion(2))
        advanceUntilIdle()
        viewModel.onEvent(DetalleRutaUiEvent.ErrorMostrado)

        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `cargar dos veces la misma ruta no reabre la observacion`() = runTest {
        cargar()
        advanceUntilIdle()
        cargar()
        advanceUntilIdle()

        // La guarda existe para que volver de la pantalla de cuadre no reinicie
        // la carga y haga parpadear el spinner sobre datos que ya estaban.
        io.mockk.verify(exactly = 1) { observeHojaRutaUseCase(7) }
    }
}
