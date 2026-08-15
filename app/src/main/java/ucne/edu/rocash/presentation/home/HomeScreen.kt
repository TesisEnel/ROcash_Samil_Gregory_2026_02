package ucne.edu.rocash.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCrearRuta: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToRecolectores: () -> Unit,
    onNavigateToAgentes: () -> Unit,
    onNavigateToEstaciones: () -> Unit,
    onNavigateToDetalleEstacion: (Int, Int, Int, String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeBody(
        state = state,
        onNavigateToCrearRuta = onNavigateToCrearRuta,
        onNavigateToHistorial = onNavigateToHistorial,
        onNavigateToRecolectores = onNavigateToRecolectores,
        onNavigateToAgentes = onNavigateToAgentes,
        onNavigateToEstaciones = onNavigateToEstaciones,
        onNavigateToDetalleEstacion = onNavigateToDetalleEstacion,
        onNavigateToProfile = onNavigateToProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBody(
    state: HomeUIState,
    onNavigateToCrearRuta: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToRecolectores: () -> Unit,
    onNavigateToAgentes: () -> Unit,
    onNavigateToEstaciones: () -> Unit,
    onNavigateToDetalleEstacion: (Int, Int, Int, String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
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
                    icon = { Icon(Icons.Default.PersonSearch, contentDescription = "Recolectores") },
                    label = { Text("Recolectores") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToRecolectores()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.SupportAgent, contentDescription = "Agentes") },
                    label = { Text("Agentes de Ventas") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToAgentes()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Store, contentDescription = "Estaciones") },
                    label = { Text("Estaciones (Bancas)") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToEstaciones()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToHistorial) {
                            Icon(Icons.Default.History, contentDescription = "Historial")
                        }
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Mi Perfil",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (state.hojaRutaActiva == null && !state.isLoading) {
                    FloatingActionButton(
                        onClick = onNavigateToCrearRuta,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva Ruta")
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Ingresos Totales",
                        valor = "$${state.totalIngresos}",
                        icono = Icons.Default.MonetizationOn
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Rutas Listas",
                        valor = "${state.rutasCompletadas}",
                        icono = Icons.Default.Route
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Ruta en Progreso", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (state.hojaRutaActiva == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Route,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No tienes ninguna ruta activa.", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = onNavigateToCrearRuta) {
                                Text("Iniciar Nueva Ruta")
                            }
                        }
                    }
                } else {
                    val ruta = state.hojaRutaActiva
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ruta #${ruta.id}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Estaciones asignadas: ${ruta.estaciones.size}", style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(ruta.estaciones) { estacion ->
                                    ListItem(
                                        modifier = Modifier
                                            .clip(MaterialTheme.shapes.medium)
                                            .clickable {
                                                onNavigateToDetalleEstacion(
                                                    ruta.id,
                                                    estacion.estacionId,
                                                    estacion.agenteId,
                                                    estacion.nombre
                                                )
                                            },
                                        headlineContent = { Text(estacion.nombre, fontWeight = FontWeight.SemiBold) },
                                        supportingContent = { Text(estacion.direccion) },
                                        trailingContent = {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = "Ir a cobrar"
                                            )
                                        },
                                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        tonalElevation = 2.dp,
                                        shadowElevation = 1.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, titulo: String, valor: String, icono: androidx.compose.ui.graphics.vector.ImageVector) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = titulo, style = MaterialTheme.typography.labelMedium)
            Text(text = valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeBodyPreview() {
    MaterialTheme {
        HomeBody(
            state = HomeUIState(
                isLoading = false,
                totalIngresos = 0.0,
                rutasCompletadas = 0,
                hojaRutaActiva = null
            ),
            onNavigateToCrearRuta = {},
            onNavigateToHistorial = {},
            onNavigateToRecolectores = {},
            onNavigateToAgentes = {},
            onNavigateToEstaciones = {},
            onNavigateToDetalleEstacion = { _, _, _, _ -> },
            onNavigateToProfile = {}
        )
    }
}