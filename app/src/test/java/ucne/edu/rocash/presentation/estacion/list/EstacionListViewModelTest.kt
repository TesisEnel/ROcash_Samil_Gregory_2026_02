package ucne.edu.rocash.presentation.estacion.list

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
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
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.usecase.DeleteEstacionUseCase
import ucne.edu.rocash.domain.estacion.usecase.ObserveEstacionesUseCase
import ucne.edu.rocash.domain.estacion.usecase.SearchEstacionesUseCase
import ucne.edu.rocash.presentation.util.MainDispatcherRule

@ExperimentalCoroutinesApi
class EstacionListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var observeEstacionesUseCase: ObserveEstacionesUseCase
    private lateinit var searchEstacionesUseCase: SearchEstacionesUseCase
    private lateinit var deleteEstacionUseCase: DeleteEstacionUseCase
    private lateinit var viewModel: EstacionListViewModel

    private val norte = EstacionVentas(1, "Banca Norte", "Av. Principal 12", agenteId = 1)
    private val sur = EstacionVentas(2, "Banca Sur", "Calle 8 esq. Duarte", agenteId = 2)

    @Before
    fun setup() {
        observeEstacionesUseCase = mockk()
        searchEstacionesUseCase = mockk()
        deleteEstacionUseCase = mockk(relaxed = true)

        every { observeEstacionesUseCase() } returns flowOf(listOf(norte, sur))

        viewModel = EstacionListViewModel(
            observeEstacionesUseCase,
            searchEstacionesUseCase,
            deleteEstacionUseCase
        )
    }

    @Test
    fun `al iniciar carga las bancas`() = runTest {
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.estaciones.size)
    }

    @Test
    fun `escribir en el buscador cambia a la busqueda`() = runTest {
        every { searchEstacionesUseCase("Norte") } returns flowOf(listOf(norte))
        advanceUntilIdle()

        viewModel.onEvent(EstacionListUiEvent.SearchQueryChanged("Norte"))
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.estaciones.size)
        assertEquals("Norte", viewModel.state.value.searchQuery)
    }

    @Test
    fun `vaciar el buscador vuelve a la lista completa`() = runTest {
        every { searchEstacionesUseCase("Norte") } returns flowOf(listOf(norte))
        advanceUntilIdle()

        viewModel.onEvent(EstacionListUiEvent.SearchQueryChanged("Norte"))
        advanceUntilIdle()
        viewModel.onEvent(EstacionListUiEvent.SearchQueryChanged(""))
        advanceUntilIdle()

        assertEquals(2, viewModel.state.value.estaciones.size)
    }

    @Test
    fun `NavegacionConsumida apaga las dos banderas`() = runTest {
        // Regresión del rebote: sin esto, al volver del formulario la lista
        // entra de nuevo en composición, el LaunchedEffect se vuelve a ejecutar
        // con la bandera aún encendida y devuelve al usuario al formulario.
        advanceUntilIdle()

        viewModel.onEvent(EstacionListUiEvent.CreateNew)
        assertTrue(viewModel.state.value.navigateToCreate)
        viewModel.onEvent(EstacionListUiEvent.NavegacionConsumida)
        assertFalse(viewModel.state.value.navigateToCreate)

        viewModel.onEvent(EstacionListUiEvent.Edit(1))
        assertEquals(1, viewModel.state.value.navigateToEditId)
        viewModel.onEvent(EstacionListUiEvent.NavegacionConsumida)
        assertNull(viewModel.state.value.navigateToEditId)
    }

    @Test
    fun `Delete elimina y avisa`() = runTest {
        coEvery { deleteEstacionUseCase(1) } just Runs
        advanceUntilIdle()

        viewModel.onEvent(EstacionListUiEvent.Delete(1))
        advanceUntilIdle()

        coVerify(exactly = 1) { deleteEstacionUseCase(1) }
        assertEquals("Estación eliminada", viewModel.state.value.message)
    }

    @Test
    fun `ClearMessage apaga el aviso`() = runTest {
        advanceUntilIdle()

        viewModel.onEvent(EstacionListUiEvent.ShowMessage("algo"))
        viewModel.onEvent(EstacionListUiEvent.ClearMessage)

        assertNull(viewModel.state.value.message)
    }
}
