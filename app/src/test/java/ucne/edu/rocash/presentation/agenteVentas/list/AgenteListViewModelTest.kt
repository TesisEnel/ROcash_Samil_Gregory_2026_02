package ucne.edu.rocash.presentation.agenteVentas.list

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.Runs
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.usecase.DeleteAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.ObserveAgentesUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.SearchAgentesUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.UpsertAgenteUseCase
import ucne.edu.rocash.presentation.util.MainDispatcherRule

@ExperimentalCoroutinesApi
class AgenteListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var observeAgentesUseCase: ObserveAgentesUseCase
    private lateinit var searchAgentesUseCase: SearchAgentesUseCase
    private lateinit var deleteAgenteUseCase: DeleteAgenteUseCase
    private lateinit var upsertAgenteUseCase: UpsertAgenteUseCase
    private lateinit var viewModel: AgenteListViewModel

    private val ramon = AgenteVentas(1, "Ramón Peralta", "8095551234", deudaAcumulada = 5_000.0)
    private val yaneris = AgenteVentas(2, "Yaneris Gómez", "8095559876")

    @Before
    fun setup() {
        observeAgentesUseCase = mockk()
        searchAgentesUseCase = mockk()
        deleteAgenteUseCase = mockk(relaxed = true)
        upsertAgenteUseCase = mockk(relaxed = true)

        every { observeAgentesUseCase() } returns flowOf(listOf(ramon, yaneris))

        viewModel = AgenteListViewModel(
            observeAgentesUseCase,
            searchAgentesUseCase,
            deleteAgenteUseCase,
            upsertAgenteUseCase
        )
    }

    @Test
    fun `al iniciar carga los agentes`() = runTest {
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.agentes.size)
        assertEquals("Ramón Peralta", state.agentes[0].nombre)
    }

    @Test
    fun `escribir en el buscador cambia a la busqueda`() = runTest {
        every { searchAgentesUseCase("Ram") } returns flowOf(listOf(ramon))
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.SearchQueryChanged("Ram"))
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.agentes.size)
        assertEquals("Ram", viewModel.state.value.searchQuery)
    }

    @Test
    fun `tocar un agente abre el menu de acciones sin navegar`() = runTest {
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.AgenteTocado(ramon))

        val state = viewModel.state.value
        assertEquals(ramon, state.agenteSeleccionado)
        assertNull(state.navigateToEditId)
        assertNull(state.navigateToDeudaId)
    }

    @Test
    fun `cerrar el menu no navega a ningun lado`() = runTest {
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.AgenteTocado(ramon))
        viewModel.onEvent(AgenteListUiEvent.CerrarAcciones)

        val state = viewModel.state.value
        assertNull(state.agenteSeleccionado)
        assertNull(state.navigateToEditId)
        assertNull(state.navigateToDeudaId)
    }

    @Test
    fun `elegir gestionar deuda navega y cierra el menu`() = runTest {
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.AgenteTocado(ramon))
        viewModel.onEvent(AgenteListUiEvent.GestionarDeuda(1))

        val state = viewModel.state.value
        assertEquals(1, state.navigateToDeudaId)
        assertNull(state.agenteSeleccionado)
    }

    @Test
    fun `elegir editar navega y cierra el menu`() = runTest {
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.AgenteTocado(ramon))
        viewModel.onEvent(AgenteListUiEvent.Edit(1))

        val state = viewModel.state.value
        assertEquals(1, state.navigateToEditId)
        assertNull(state.agenteSeleccionado)
    }

    @Test
    fun `NavegacionConsumida apaga las tres banderas`() = runTest {
        // Es la regresion del bug que dejaba al usuario atrapado: si estas
        // banderas no se apagan, al volver del formulario la lista entra de
        // nuevo en composicion, el LaunchedEffect se ejecuta otra vez y rebota
        // al usuario a la pantalla de la que acaba de salir.
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.CreateNew)
        viewModel.onEvent(AgenteListUiEvent.NavegacionConsumida)
        assertFalse(viewModel.state.value.navigateToCreate)

        viewModel.onEvent(AgenteListUiEvent.Edit(1))
        viewModel.onEvent(AgenteListUiEvent.NavegacionConsumida)
        assertNull(viewModel.state.value.navigateToEditId)

        viewModel.onEvent(AgenteListUiEvent.GestionarDeuda(1))
        viewModel.onEvent(AgenteListUiEvent.NavegacionConsumida)
        assertNull(viewModel.state.value.navigateToDeudaId)
    }

    @Test
    fun `CreateNew enciende la navegacion a crear`() = runTest {
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.CreateNew)

        assertTrue(viewModel.state.value.navigateToCreate)
    }

    @Test
    fun `Delete elimina y avisa`() = runTest {
        coEvery { deleteAgenteUseCase(1) } just Runs
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.Delete(1))
        advanceUntilIdle()

        coVerify(exactly = 1) { deleteAgenteUseCase(1) }
        assertEquals("Agente eliminado", viewModel.state.value.message)
    }

    @Test
    fun `ClearMessage apaga el aviso`() = runTest {
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.ShowMessage("algo"))
        viewModel.onEvent(AgenteListUiEvent.ClearMessage)

        assertNull(viewModel.state.value.message)
    }

    @Test
    fun `dar de baja a un agente conserva su deuda`() = runTest {
        advanceUntilIdle()

        viewModel.onEvent(AgenteListUiEvent.ToggleEstado(ramon))
        advanceUntilIdle()

        // Ramón debe 5000. Cambiarle el estado no puede borrarle la deuda:
        // ese fue exactamente el bug del formulario de agentes.
        coVerify(exactly = 1) {
            upsertAgenteUseCase(
                match { it.agenteId == 1 && !it.estado && it.deudaAcumulada == 5_000.0 }
            )
        }
    }
}
