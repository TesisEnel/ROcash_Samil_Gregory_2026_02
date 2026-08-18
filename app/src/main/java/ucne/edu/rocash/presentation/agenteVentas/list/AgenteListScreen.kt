package ucne.edu.rocash.presentation.agenteVentas.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas


@Composable
fun AgenteListScreen(
    viewModel: AgenteListViewModel = hiltViewModel(),
    onAbrirMenu: () -> Unit,
    onNavigateToCrear: () -> Unit,
    onNavigateToEditar: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToCreate) {
        if (state.navigateToCreate) {
            onNavigateToCrear()
            viewModel.onEvent(AgenteListUiEvent.NavegacionConsumida)
        }
    }

    LaunchedEffect(state.navigateToEditId) {
        state.navigateToEditId?.let { id ->
            onNavigateToEditar(id)
            viewModel.onEvent(AgenteListUiEvent.NavegacionConsumida)
        }
    }

    AgenteListBody(
        state = state,
        onEvent = viewModel::onEvent,
        onAbrirMenu = onAbrirMenu
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenteListBody(
    state: AgenteListUiState,
    onEvent: (AgenteListUiEvent) -> Unit,
    onAbrirMenu: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onEvent(AgenteListUiEvent.ClearMessage)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Agentes") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(AgenteListUiEvent.CreateNew) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Agente")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onEvent(AgenteListUiEvent.SearchQueryChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("search_input"),
                    placeholder = { Text("Buscar agente...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.testTag("loading"))
                    }
                } else if (state.agentes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay agentes",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.testTag("empty_message")
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = state.agentes, key = { it.agenteId }) { agente ->
                            AgenteItem(
                                agente = agente,
                                onEvent = onEvent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgenteItem(
    agente: AgenteVentas,
    onEvent: (AgenteListUiEvent) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("agente_item_${agente.agenteId}")
            .clickable { onEvent(AgenteListUiEvent.Edit(agente.agenteId)) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (agente.estado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = agente.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = agente.telefono, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Deuda: $${agente.deudaAcumulada}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (agente.deudaAcumulada > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            Switch(
                checked = agente.estado,
                onCheckedChange = { onEvent(AgenteListUiEvent.ToggleEstado(agente)) },
                modifier = Modifier.testTag("switch_estado_${agente.agenteId}")
            )
            IconButton(
                onClick = { onEvent(AgenteListUiEvent.Delete(agente.agenteId)) },
                modifier = Modifier.testTag("btn_delete_${agente.agenteId}")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AgenteListBodyEmptyPreview() {
    MaterialTheme {
        AgenteListBody(
            state = AgenteListUiState(
                isLoading = false,
                agentes = emptyList()
            ),
            onEvent = {},
            onAbrirMenu = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AgenteListBodyContentPreview() {
    MaterialTheme {
        AgenteListBody(
            state = AgenteListUiState(
                isLoading = false,
                agentes = listOf(
                    AgenteVentas(
                        agenteId = 1,
                        nombre = "Juan Pérez",
                        telefono = "809-555-1234",
                        deudaAcumulada = 1500.0,
                        estado = true
                    ),
                    AgenteVentas(
                        agenteId = 2,
                        nombre = "María Gómez",
                        telefono = "829-555-9876",
                        deudaAcumulada = 0.0,
                        estado = false
                    )
                )
            ),
            onEvent = {},
            onAbrirMenu = {}
        )
    }
}