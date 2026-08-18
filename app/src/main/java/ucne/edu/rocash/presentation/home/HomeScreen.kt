package ucne.edu.rocash.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.hojaRuta.model.EstacionEnRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.presentation.common.EstadoRutaChip
import ucne.edu.rocash.presentation.common.aFechaLegible
import ucne.edu.rocash.presentation.common.aMoneda

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAbrirMenu: () -> Unit,
    onNavigateToCrearRuta: () -> Unit,
    onNavigateToDetalleRuta: (Int) -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToAgentes: () -> Unit,
    onNavigateToEstaciones: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeBody(
        state = state,
        onEvent = viewModel::onEvent,
        onAbrirMenu = onAbrirMenu,
        onNavigateToCrearRuta = onNavigateToCrearRuta,
        onNavigateToDetalleRuta = onNavigateToDetalleRuta,
        onNavigateToHistorial = onNavigateToHistorial,
        onNavigateToProfile = onNavigateToProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBody(
    state: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    onAbrirMenu: () -> Unit,
    onNavigateToCrearRuta: () -> Unit,
    onNavigateToDetalleRuta: (Int) -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            onEvent(HomeUiEvent.ErrorMostrado)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToHistorial,
                        modifier = Modifier.testTag("btn_historial")
                    ) {
                        Icon(Icons.Default.History, contentDescription = "Historial")
                    }
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.testTag("btn_perfil")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Mi perfil",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.mostrarAccionNuevaRuta) {
                FloatingActionButton(
                    onClick = onNavigateToCrearRuta,
                    modifier = Modifier.testTag("fab_nueva_ruta")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva ruta")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("loading")
                )

                state.sinSesion -> Text(
                    text = "No hay una sesión activa. Vuelve a iniciar sesión.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .testTag("sin_sesion")
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                titulo = "Ingresos totales",
                                valor = state.totalIngresos.aMoneda(),
                                icono = Icons.Default.MonetizationOn
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                titulo = "Rutas cerradas",
                                valor = state.rutasCompletadas.toString(),
                                icono = Icons.Default.Route
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Rutas en curso",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (!state.hayRutasAbiertas) {
                        item { RutasVaciasCard(onNavigateToCrearRuta = onNavigateToCrearRuta) }
                    } else {
                        items(
                            items = state.rutasAbiertas,
                            key = { it.id }
                        ) { ruta ->
                            RutaAbiertaCard(
                                ruta = ruta,
                                onClick = { onNavigateToDetalleRuta(ruta.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RutaAbiertaCard(
    ruta: HojaRuta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("ruta_item_${ruta.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ruta #${ruta.id}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = ruta.fechaCreacion.aFechaLegible(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                EstadoRutaChip(estado = ruta.estado)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            if (ruta.cantidadEstaciones > 0) {
                Text(
                    text = "${ruta.estacionesCuadradas} de ${ruta.cantidadEstaciones} bancas cuadradas",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )

                LinearProgressIndicator(
                    progress = { ruta.porcentajeAvance },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .testTag("progreso_ruta_${ruta.id}")
                )
            }
        }
    }
}

@Composable
private fun RutasVaciasCard(
    onNavigateToCrearRuta: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Route,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No tienes rutas en curso.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(
                onClick = onNavigateToCrearRuta,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("btn_iniciar_ruta")
            ) {
                Text("Armar nueva ruta")
            }
        }
    }
}

@Composable
fun StatCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Unspecified
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeBodyPreview() {
    val banca = { id: Int, nombre: String ->
        EstacionEnRuta(
            estacion = EstacionVentas(id, nombre, "Calle $id", agenteId = 1),
            orden = id,
            estado = if (id % 2 == 0) EstadoVisitaEstacion.COMPLETADA
            else EstadoVisitaEstacion.PENDIENTE
        )
    }

    MaterialTheme {
        HomeBody(
            state = HomeUiState(
                isLoading = false,
                rutasAbiertas = listOf(
                    HojaRuta(
                        id = 7,
                        recolectorId = "uid",
                        estado = EstadoRuta.EN_PROGRESO,
                        estaciones = listOf(banca(1, "Banca Norte"), banca(2, "Banca Sur"))
                    ),
                    HojaRuta(
                        id = 8,
                        recolectorId = "uid",
                        estado = EstadoRuta.PENDIENTE,
                        estaciones = listOf(banca(3, "Banca Central"))
                    )
                ),
                hayRutasAbiertas = true,
                totalIngresos = 184_500.0,
                rutasCompletadas = 12,
                mostrarAccionNuevaRuta = true
            ),
            onEvent = {},
            onAbrirMenu = {},
            onNavigateToCrearRuta = {},
            onNavigateToDetalleRuta = {},
            onNavigateToHistorial = {},
            onNavigateToProfile = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeBodyVacioPreview() {
    MaterialTheme {
        HomeBody(
            state = HomeUiState(
                isLoading = false,
                rutasAbiertas = emptyList(),
                hayRutasAbiertas = false,
                totalIngresos = 0.0,
                rutasCompletadas = 0,
                mostrarAccionNuevaRuta = true
            ),
            onEvent = {},
            onAbrirMenu = {},
            onNavigateToCrearRuta = {},
            onNavigateToDetalleRuta = {},
            onNavigateToHistorial = {},
            onNavigateToProfile = {}
        )
    }
}