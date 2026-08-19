package ucne.edu.rocash.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Regresión del congelamiento del login.
 *
 * El drawer se componía siempre, incluso en Login y Registro, con la hoja
 * vacía. `ModalNavigationDrawer` calcula la fracción de apertura del velo
 * dividiendo por el ancho de esa hoja: con ancho cero la fracción quedaba en
 * NaN, el velo se pintaba sobre toda la pantalla y se comía cada toque. La
 * pantalla se veía bien pero no respondía a nada.
 *
 * El primer test es el que fallaba antes del arreglo: el botón está visible
 * pero el clic nunca llega.
 */
class RoCashDrawerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sin_menu_el_contenido_sigue_recibiendo_toques() {
        var tocado = false

        composeTestRule.setContent {
            RoCashDrawer(
                drawerState = rememberDrawerState(DrawerValue.Closed),
                gesturesEnabled = false,
                currentRoute = "AuthRoute",
                onNavigateToHome = {},
                onNavigateToAgentes = {},
                onNavigateToEstaciones = {}
            ) {
                Text(
                    text = "Iniciar Sesión",
                    modifier = Modifier
                        .testTag("btn_login")
                        .clickable { tocado = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("btn_login").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_login").performClick()

        assertTrue("El velo del drawer vacío está interceptando los toques", tocado)
    }

    @Test
    fun sin_menu_no_se_pinta_el_menu() {
        composeTestRule.setContent {
            RoCashDrawer(
                drawerState = rememberDrawerState(DrawerValue.Open),
                gesturesEnabled = false,
                currentRoute = "AuthRoute",
                onNavigateToHome = {},
                onNavigateToAgentes = {},
                onNavigateToEstaciones = {}
            ) {
                Text("Contenido", modifier = Modifier.testTag("contenido"))
            }
        }

        composeTestRule.onNodeWithTag("contenido").assertIsDisplayed()
        composeTestRule.onNodeWithText("Menú RoCash").assertDoesNotExist()
    }

    @Test
    fun con_menu_abierto_se_ven_las_tres_opciones() {
        composeTestRule.setContent {
            RoCashDrawer(
                drawerState = rememberDrawerState(DrawerValue.Open),
                gesturesEnabled = true,
                currentRoute = "HomeRecolectorRoute",
                onNavigateToHome = {},
                onNavigateToAgentes = {},
                onNavigateToEstaciones = {}
            ) {
                Text("Contenido")
            }
        }

        composeTestRule.onNodeWithText("Dashboard (Inicio)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Agentes de Ventas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Estaciones (Bancas)").assertIsDisplayed()
    }

    @Test
    fun tocar_agentes_dispara_su_navegacion() {
        var navego = false

        composeTestRule.setContent {
            RoCashDrawer(
                drawerState = rememberDrawerState(DrawerValue.Open),
                gesturesEnabled = true,
                currentRoute = "HomeRecolectorRoute",
                onNavigateToHome = {},
                onNavigateToAgentes = { navego = true },
                onNavigateToEstaciones = {}
            ) {
                Text("Contenido")
            }
        }

        composeTestRule.onNodeWithText("Agentes de Ventas").performClick()

        assertTrue(navego)
    }
}
