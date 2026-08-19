package ucne.edu.rocash.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RoCashDrawer(
    drawerState: DrawerState,
    gesturesEnabled: Boolean,
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToAgentes: () -> Unit,
    onNavigateToEstaciones: () -> Unit,
    content: @Composable () -> Unit
) {
    if (!gesturesEnabled) {
        content()
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú RoCash",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Dashboard (Inicio)") },
                    selected = currentRoute.contains("HomeRecolectorRoute"),
                    onClick = onNavigateToHome,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.SupportAgent, contentDescription = null) },
                    label = { Text("Agentes de Ventas") },
                    selected = currentRoute.contains("AgenteListRoute"),
                    onClick = onNavigateToAgentes,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Store, contentDescription = null) },
                    label = { Text("Estaciones (Bancas)") },
                    selected = currentRoute.contains("EstacionListRoute"),
                    onClick = onNavigateToEstaciones,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
        content = content
    )
}
