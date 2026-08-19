package ucne.edu.rocash.presentation.agenteVentas.form

import androidx.lifecycle.SavedStateHandle
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.usecase.DeleteAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.GetAgenteUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.UpsertAgenteUseCase
import ucne.edu.rocash.presentation.util.MainDispatcherRule

/**
 * Este formulario borró deuda real en producción: construía el AgenteVentas con
 * `deudaAcumulada = 0.0` y `estado = true` fijos, así que cambiarle el teléfono
 * a un agente le ponía la deuda en cero y reactivaba a los dados de baja.
 *
 * Los dos tests de conservación existen precisamente para que eso no vuelva a
 * pasar sin que nadie se entere.
 */
@ExperimentalCoroutinesApi
class AgenteFormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getAgenteUseCase: GetAgenteUseCase
    private lateinit var upsertAgenteUseCase: UpsertAgenteUseCase
    private lateinit var deleteAgenteUseCase: DeleteAgenteUseCase

    private val agenteExistente = AgenteVentas(
        agenteId = 1,
        nombre = "Ramón Peralta",
        telefono = "8095551234",
        deudaAcumulada = 7_500.0,
        estado = false
    )

    @Before
    fun setup() {
        getAgenteUseCase = mockk()
        upsertAgenteUseCase = mockk()
        deleteAgenteUseCase = mockk(relaxed = true)

        coEvery { getAgenteUseCase(1) } returns agenteExistente
        coEvery { upsertAgenteUseCase(any()) } returns Result.success(1)
    }

    private fun crearViewModel(agenteId: Int? = null) = AgenteFormViewModel(
        getAgenteUseCase,
        upsertAgenteUseCase,
        deleteAgenteUseCase,
        SavedStateHandle(agenteId?.let { mapOf("agenteId" to it) } ?: emptyMap())
    )

    @Test
    fun `sin id arranca en modo alta`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isNew)
        assertNull(state.agenteId)
        assertEquals("", state.nombre)
    }

    @Test
    fun `con id carga los datos del agente`() = runTest {
        val viewModel = crearViewModel(agenteId = 1)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isNew)
        assertEquals("Ramón Peralta", state.nombre)
        assertEquals("8095551234", state.telefono)
    }

    @Test
    fun `editar el telefono conserva la deuda acumulada`() = runTest {
        val viewModel = crearViewModel(agenteId = 1)
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.TelefonoChanged("8095559999"))
        viewModel.onEvent(AgenteFormUiEvent.Save)
        advanceUntilIdle()

        // Ramón debe 7500. Si el guardado los pone en cero, la agencia pierde
        // el registro de lo que el agente adeuda.
        coVerify(exactly = 1) {
            upsertAgenteUseCase(match { it.deudaAcumulada == 7_500.0 })
        }
    }

    @Test
    fun `editar a un agente dado de baja no lo reactiva`() = runTest {
        val viewModel = crearViewModel(agenteId = 1)
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.NombreChanged("Ramón A. Peralta"))
        viewModel.onEvent(AgenteFormUiEvent.Save)
        advanceUntilIdle()

        coVerify(exactly = 1) { upsertAgenteUseCase(match { !it.estado }) }
    }

    @Test
    fun `un agente nuevo nace activo y sin deuda`() = runTest {
        coEvery { upsertAgenteUseCase(any()) } returns Result.success(9)

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.NombreChanged("Yaneris Gómez"))
        viewModel.onEvent(AgenteFormUiEvent.TelefonoChanged("8095559876"))
        viewModel.onEvent(AgenteFormUiEvent.Save)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            upsertAgenteUseCase(
                match { it.agenteId == 0 && it.estado && it.deudaAcumulada == 0.0 }
            )
        }
    }

    @Test
    fun `un alta exitosa recibe el id generado y sale de modo alta`() = runTest {
        coEvery { upsertAgenteUseCase(any()) } returns Result.success(9)

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.NombreChanged("Yaneris Gómez"))
        viewModel.onEvent(AgenteFormUiEvent.TelefonoChanged("8095559876"))
        viewModel.onEvent(AgenteFormUiEvent.Save)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.saved)
        assertFalse(state.isSaving)
        // Si el id se quedara en 0, guardar otra vez desde la misma pantalla
        // crearía un segundo agente en lugar de actualizar el primero.
        assertEquals(9, state.agenteId)
        assertFalse(state.isNew)
    }

    @Test
    fun `un nombre corto marca el error y no guarda`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.NombreChanged("Jo"))
        viewModel.onEvent(AgenteFormUiEvent.TelefonoChanged("8095551234"))
        viewModel.onEvent(AgenteFormUiEvent.Save)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.nombreError)
        assertFalse(viewModel.state.value.saved)
        coVerify(exactly = 0) { upsertAgenteUseCase(any()) }
    }

    @Test
    fun `un telefono corto marca el error y no guarda`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.NombreChanged("Yaneris Gómez"))
        viewModel.onEvent(AgenteFormUiEvent.TelefonoChanged("809555"))
        viewModel.onEvent(AgenteFormUiEvent.Save)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.telefonoError)
        coVerify(exactly = 0) { upsertAgenteUseCase(any()) }
    }

    @Test
    fun `escribir en un campo limpia solo su propio error`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.NombreChanged("Jo"))
        viewModel.onEvent(AgenteFormUiEvent.TelefonoChanged("809"))
        viewModel.onEvent(AgenteFormUiEvent.Save)
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.NombreChanged("Yaneris Gómez"))

        assertNull(viewModel.state.value.nombreError)
        assertNotNull(viewModel.state.value.telefonoError)
    }

    @Test
    fun `un fallo al guardar se muestra en vez de tragarse`() = runTest {
        coEvery { upsertAgenteUseCase(any()) } returns
                Result.failure(IllegalStateException("UNIQUE constraint failed"))

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.NombreChanged("Yaneris Gómez"))
        viewModel.onEvent(AgenteFormUiEvent.TelefonoChanged("8095559876"))
        viewModel.onEvent(AgenteFormUiEvent.Save)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("UNIQUE constraint failed", state.errorMessage)
        assertFalse(state.saved)
        assertFalse(state.isSaving)
    }

    @Test
    fun `ErrorMostrado apaga el mensaje`() = runTest {
        coEvery { upsertAgenteUseCase(any()) } returns
                Result.failure(IllegalStateException("boom"))

        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.NombreChanged("Yaneris Gómez"))
        viewModel.onEvent(AgenteFormUiEvent.TelefonoChanged("8095559876"))
        viewModel.onEvent(AgenteFormUiEvent.Save)
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.ErrorMostrado)

        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `borrar marca deleted y llama al caso de uso`() = runTest {
        coEvery { deleteAgenteUseCase(1) } just Runs

        val viewModel = crearViewModel(agenteId = 1)
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.Delete)
        advanceUntilIdle()

        coVerify(exactly = 1) { deleteAgenteUseCase(1) }
        assertTrue(viewModel.state.value.deleted)
        assertFalse(viewModel.state.value.isDeleting)
    }

    @Test
    fun `borrar un agente que aun no existe no llama a nada`() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AgenteFormUiEvent.Delete)
        advanceUntilIdle()

        coVerify(exactly = 0) { deleteAgenteUseCase(any()) }
        assertFalse(viewModel.state.value.deleted)
    }
}
