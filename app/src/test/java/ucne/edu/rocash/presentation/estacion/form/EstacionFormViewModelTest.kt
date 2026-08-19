package ucne.edu.rocash.presentation.estacion.form

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.usecase.ObserveAgentesUseCase
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.usecase.DeleteEstacionUseCase
import ucne.edu.rocash.domain.estacion.usecase.GetEstacionUseCase
import ucne.edu.rocash.domain.estacion.usecase.UpsertEstacionUseCase
import ucne.edu.rocash.presentation.util.MainDispatcherRule

/**
 * Cubre el desplegable de agentes, que es donde se reportó que un agente recién
 * creado no aparecía. El flujo se prueba de punta a punta: emitir un agente
 * nuevo por el Flow y comprobar que llega al estado.
 */
@ExperimentalCoroutinesApi
class EstacionFormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var upsertEstacionUseCase: UpsertEstacionUseCase
    private lateinit var getEstacionUseCase: GetEstacionUseCase
    private lateinit var deleteEstacionUseCase: DeleteEstacionUseCase
    private lateinit var observeAgentesUseCase: ObserveAgentesUseCase

    private val agentes = MutableStateFlow(
        listOf(AgenteVentas(1, "Ramón Peralta", "8095551234"))
    )

    @Before
    fun setup() {
        upsertEstacionUseCase = mockk()
        getEstacionUseCase = mockk()
        deleteEstacionUseCase = mockk(relaxed = true)
        observeAgentesUseCase = mockk()

        every { observeAgentesUseCase() } returns agentes
        coEvery { upsertEstacionUseCase(any()) } returns Result.success(9)
    }

    private fun crearViewModel(estacionId: Int? = null) = EstacionFormViewModel(
        upsertEstacionUseCase,
        getEstacionUseCase,
        deleteEstacionUseCase,
        observeAgentesUseCase,
        SavedStateHandle(estacionId?.let { mapOf("estacionId" to it) } ?: emptyMap())
    )

    @Test
    fun `el desplegable trae los agentes activos`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.agentesDisponibles.size)
        assertEquals("Ramón Peralta", viewModel.state.value.agentesDisponibles[0].nombre)
    }

    @Test
    fun `un agente creado despues aparece en el desplegable sin recargar`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.agentesDisponibles.size)

        // Simula el alta de un agente nuevo mientras la pantalla está abierta.
        agentes.value = agentes.value + AgenteVentas(2, "Yaneris Gómez", "8095559876")
        advanceUntilIdle()

        val disponibles = viewModel.state.value.agentesDisponibles
        assertEquals(2, disponibles.size)
        assertNotNull(disponibles.find { it.agenteId == 2 })
    }

    @Test
    fun `un agente dado de baja desaparece del desplegable`() = runTest {
        // Es la contraparte del caso anterior y explica por qué un agente puede
        // "no aparecer": basta que su estado esté en false.
        val viewModel = crearViewModel()
        advanceUntilIdle()

        agentes.value = listOf(AgenteVentas(1, "Ramón Peralta", "8095551234", estado = false))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.agentesDisponibles.isEmpty())
    }

    @Test
    fun `una estacion nueva arranca en modo alta`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isNew)
    }

    @Test
    fun `al editar carga la estacion y resuelve el nombre del agente`() = runTest {
        coEvery { getEstacionUseCase(5) } returns EstacionVentas(
            estacionId = 5,
            nombre = "Banca Norte",
            direccion = "Av. Principal 12",
            agenteId = 1
        )

        val viewModel = crearViewModel(estacionId = 5)
        advanceUntilIdle()

        // Reemitir la lista fuerza la resolución del nombre, igual que ocurre
        // cuando Room vuelve a emitir tras cargar la estación.
        agentes.value = agentes.value.toList()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isNew)
        assertEquals("Banca Norte", state.nombre)
        assertEquals(1, state.agenteId)
        assertEquals("Ramón Peralta", state.agenteNombreSeleccionado)
    }

    @Test
    fun `editar una banca de dos agentes conserva el segundo`() = runTest {
        coEvery { getEstacionUseCase(5) } returns EstacionVentas(
            estacionId = 5,
            nombre = "Banca Norte",
            direccion = "Av. Principal 12",
            agenteId = 1,
            agenteId2 = 4
        )

        val viewModel = crearViewModel(estacionId = 5)
        advanceUntilIdle()

        viewModel.onEvent(EstacionFormUiEvent.NombreChanged("Banca Norte II"))
        viewModel.onEvent(EstacionFormUiEvent.Save)
        advanceUntilIdle()

        // El formulario no edita agenteId2, pero antes lo omitía al construir
        // el EstacionVentas y lo borraba. El daño no era solo perder el dato:
        // `deudaSeReparte` en el cuadre depende de él, así que tras editar la
        // banca la deuda dejaba de repartirse entre los dos agentes.
        coVerify(exactly = 1) { upsertEstacionUseCase(match { it.agenteId2 == 4 }) }
    }

    @Test
    fun `una banca de un solo agente sigue sin segundo`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(EstacionFormUiEvent.NombreChanged("Banca Sur"))
        viewModel.onEvent(EstacionFormUiEvent.DireccionChanged("Calle 8"))
        viewModel.onEvent(EstacionFormUiEvent.AgenteSeleccionado(1, "Ramón Peralta"))
        viewModel.onEvent(EstacionFormUiEvent.Save)
        advanceUntilIdle()

        coVerify(exactly = 1) { upsertEstacionUseCase(match { it.agenteId2 == null }) }
    }

    @Test
    fun `guardar sin agente marca el error y no llama al caso de uso`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(EstacionFormUiEvent.NombreChanged("Banca Sur"))
        viewModel.onEvent(EstacionFormUiEvent.DireccionChanged("Calle 8"))
        viewModel.onEvent(EstacionFormUiEvent.Save)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.agenteError)
        assertFalse(viewModel.state.value.saved)
        coVerify(exactly = 0) { upsertEstacionUseCase(any()) }
    }

    @Test
    fun `guardar sin nombre ni direccion marca ambos errores`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(EstacionFormUiEvent.AgenteSeleccionado(1, "Ramón Peralta"))
        viewModel.onEvent(EstacionFormUiEvent.Save)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.nombreError)
        assertNotNull(state.direccionError)
        coVerify(exactly = 0) { upsertEstacionUseCase(any()) }
    }

    @Test
    fun `un alta valida guarda y recibe el id generado`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(EstacionFormUiEvent.NombreChanged("Banca Sur"))
        viewModel.onEvent(EstacionFormUiEvent.DireccionChanged("Calle 8 esq. Duarte"))
        viewModel.onEvent(EstacionFormUiEvent.AgenteSeleccionado(1, "Ramón Peralta"))
        viewModel.onEvent(EstacionFormUiEvent.Save)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.saved)
        assertFalse(state.isSaving)
        // El id debe venir del repositorio, no quedarse en 0: si no, guardar
        // otra vez desde la misma pantalla crearía un duplicado.
        assertEquals(9, state.estacionId)
        assertFalse(state.isNew)
    }

    @Test
    fun `un fallo al guardar se muestra en vez de tragarse`() = runTest {
        coEvery { upsertEstacionUseCase(any()) } returns
                Result.failure(IllegalStateException("FOREIGN KEY constraint failed"))

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(EstacionFormUiEvent.NombreChanged("Banca Sur"))
        viewModel.onEvent(EstacionFormUiEvent.DireccionChanged("Calle 8"))
        viewModel.onEvent(EstacionFormUiEvent.AgenteSeleccionado(1, "Ramón Peralta"))
        viewModel.onEvent(EstacionFormUiEvent.Save)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("FOREIGN KEY constraint failed", state.errorMessage)
        assertFalse(state.saved)
        assertFalse(state.isSaving)
    }

    @Test
    fun `ErrorMostrado apaga el mensaje`() = runTest {
        coEvery { upsertEstacionUseCase(any()) } returns
                Result.failure(IllegalStateException("boom"))

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(EstacionFormUiEvent.NombreChanged("Banca Sur"))
        viewModel.onEvent(EstacionFormUiEvent.DireccionChanged("Calle 8"))
        viewModel.onEvent(EstacionFormUiEvent.AgenteSeleccionado(1, "Ramón Peralta"))
        viewModel.onEvent(EstacionFormUiEvent.Save)
        advanceUntilIdle()

        viewModel.onEvent(EstacionFormUiEvent.ErrorMostrado)

        assertEquals(null, viewModel.state.value.errorMessage)
    }
}
