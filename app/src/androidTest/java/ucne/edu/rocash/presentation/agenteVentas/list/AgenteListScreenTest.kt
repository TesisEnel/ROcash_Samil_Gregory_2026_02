package ucne.edu.rocash.presentation.agenteVentas.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

/**
 * Primer test de UI del proyecto.
 *
 * Se prueba `AgenteListBody` y no `AgenteListScreen` porque el Body recibe el
 * estado ya construido y devuelve los eventos por lambda: no necesita Hilt, ni
 * base de datos, ni ViewModel. Ese es justamente el motivo de partir cada
 * pantalla en Screen + Body.
 */
class AgenteListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val ramon = AgenteVentas(1, "Ramón Peralta", "8095551234", deudaAcumulada = 5_000.0)
    private val yaneris = AgenteVentas(2, "Yaneris Gómez", "8095559876")

    @Test
    fun muestra_el_indicador_mientras_carga() {
        composeTestRule.setContent {
            AgenteListBody(
                state = AgenteListUiState(isLoading = true),
                onEvent = {},
                onAbrirMenu = {}
            )
        }

        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun muestra_el_mensaje_vacio_cuando_no_hay_agentes() {
        composeTestRule.setContent {
            AgenteListBody(
                state = AgenteListUiState(isLoading = false, agentes = emptyList()),
                onEvent = {},
                onAbrirMenu = {}
            )
        }

        composeTestRule.onNodeWithTag("empty_message").assertIsDisplayed()
    }

    @Test
    fun muestra_los_agentes_de_la_lista() {
        composeTestRule.setContent {
            AgenteListBody(
                state = AgenteListUiState(isLoading = false, agentes = listOf(ramon, yaneris)),
                onEvent = {},
                onAbrirMenu = {}
            )
        }

        composeTestRule.onNodeWithText("Ramón Peralta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Yaneris Gómez").assertIsDisplayed()
    }

    @Test
    fun el_fab_dispara_CreateNew() {
        var eventoCapturado: AgenteListUiEvent? = null

        composeTestRule.setContent {
            AgenteListBody(
                state = AgenteListUiState(isLoading = false),
                onEvent = { eventoCapturado = it },
                onAbrirMenu = {}
            )
        }

        composeTestRule.onNodeWithTag("fab_add").performClick()

        assertTrue(eventoCapturado is AgenteListUiEvent.CreateNew)
    }

    @Test
    fun tocar_un_agente_dispara_AgenteTocado_y_no_navega_directo() {
        var eventoCapturado: AgenteListUiEvent? = null

        composeTestRule.setContent {
            AgenteListBody(
                state = AgenteListUiState(isLoading = false, agentes = listOf(ramon)),
                onEvent = { eventoCapturado = it },
                onAbrirMenu = {}
            )
        }

        composeTestRule.onNodeWithTag("agente_item_1").performClick()

        // Antes tocaba la tarjeta y se iba directo a editar. Ahora abre el menú.
        assertTrue(eventoCapturado is AgenteListUiEvent.AgenteTocado)
    }

    @Test
    fun el_menu_de_acciones_ofrece_deuda_y_edicion() {
        composeTestRule.setContent {
            AgenteListBody(
                state = AgenteListUiState(
                    isLoading = false,
                    agentes = listOf(ramon),
                    agenteSeleccionado = ramon
                ),
                onEvent = {},
                onAbrirMenu = {}
            )
        }

        composeTestRule.onNodeWithTag("btn_gestionar_deuda").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_editar_agente").assertIsDisplayed()
    }

    @Test
    fun elegir_gestionar_deuda_dispara_el_evento_con_el_id() {
        var eventoCapturado: AgenteListUiEvent? = null

        composeTestRule.setContent {
            AgenteListBody(
                state = AgenteListUiState(
                    isLoading = false,
                    agentes = listOf(ramon),
                    agenteSeleccionado = ramon
                ),
                onEvent = { eventoCapturado = it },
                onAbrirMenu = {}
            )
        }

        composeTestRule.onNodeWithTag("btn_gestionar_deuda").performClick()

        assertEquals(AgenteListUiEvent.GestionarDeuda(1), eventoCapturado)
    }

    @Test
    fun el_switch_de_estado_dispara_ToggleEstado() {
        var eventoCapturado: AgenteListUiEvent? = null

        composeTestRule.setContent {
            AgenteListBody(
                state = AgenteListUiState(isLoading = false, agentes = listOf(ramon)),
                onEvent = { eventoCapturado = it },
                onAbrirMenu = {}
            )
        }

        composeTestRule.onNodeWithTag("switch_estado_1").performClick()

        assertTrue(eventoCapturado is AgenteListUiEvent.ToggleEstado)
    }
}
