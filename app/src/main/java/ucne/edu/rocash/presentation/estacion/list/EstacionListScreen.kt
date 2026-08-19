package ucne.edu.rocash.presentation.estacion.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

@Composable
fun EstacionListScreen(
    viewModel: EstacionListViewModel = hiltViewModel(),
    onAbrirMenu: () -> Unit,
    onNavigateToCrear: () -> Unit,
    onNavigateToEditar: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToCreate) {
        if (state.navigateToCreate) {
            onNavigateToCrear()
            viewModel.onEvent(EstacionListUiEvent.NavegacionConsumida)
        }
    }

    LaunchedEffect(state.navigateToEditId) {
        state.navigateToEditId?.let { id ->
            onNavigateToEditar(id)
            viewModel.onEvent(EstacionListUiEvent.NavegacionConsumida)
        }
    }

    EstacionListBody(
        state = state,
        onEvent = viewModel::onEvent,
        onAbrirMenu = onAbrirMenu
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstacionListBody(
    state: EstacionListUiState,
    onEvent: (EstacionListUiEvent) -> Unit,
    onAbrirMenu: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onEvent(EstacionListUiEvent.ClearMessage)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Estaciones Registradas") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(EstacionListUiEvent.CreateNew) },
                containerColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.testTag("fab_add_estacion")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Estación")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(EstacionListUiEvent.SearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("search_estacion"),
                placeholder = { Text("Buscar estación...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.testTag("loading"))
                } else if (state.estaciones.isEmpty()) {
                    Text(
                        text = "No hay estaciones registradas.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("empty_message")
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = state.estaciones, key = { it.estacionId }) { estacion ->
                            EstacionItem(
                                estacion = estacion,
                                onClick = { onEvent(EstacionListUiEvent.Edit(estacion.estacionId)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstacionItem(
    estacion: EstacionVentas,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("estacion_item_${estacion.estacionId}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = estacion.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = estacion.direccion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EstacionListBodyPreview() {
    MaterialTheme {
        EstacionListBody(
            state = EstacionListUiState(
                isLoading = false,
                estaciones = listOf(
                    EstacionVentas(estacionId = 1, nombre = "Banca Principal", direccion = "Calle 1", agenteId = 1),
                    EstacionVentas(estacionId = 2, nombre = "Banca Sur", direccion = "Avenida 2", agenteId = 2)
                )
            ),
            onEvent = {},
            onAbrirMenu = {}
        )
    }
}
