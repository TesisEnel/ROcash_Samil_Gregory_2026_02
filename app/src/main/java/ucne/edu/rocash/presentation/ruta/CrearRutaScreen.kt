package ucne.edu.rocash.presentation.ruta

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

@Composable
fun CrearRutaScreen(
    viewModel: CrearRutaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateBack()
    }

    CrearRutaBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRutaBody(
    state: CrearRutaUIState,
    onEvent: (CrearRutaUIEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Armar Hoja de Ruta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(16.dp)
            ) {
                Button(
                    onClick = { onEvent(CrearRutaUIEvent.GenerarHojaRuta) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("btn_generar_ruta"),
                    enabled = !state.isSaving && state.estacionesSeleccionadas.isNotEmpty()
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Generar Ruta con ${state.estacionesSeleccionadas.size} Bancas")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).testTag("loading"))
            } else if (state.estacionesDisponibles.isEmpty()) {
                Text(
                    text = "No hay estaciones registradas en el sistema.",
                    modifier = Modifier.align(Alignment.Center).testTag("empty_message")
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        if (state.errorMessage != null) {
                            Text(
                                text = state.errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    items(
                        items = state.estacionesDisponibles,
                        key = { it.estacionId }
                    ) { estacion ->

                        val isSelected = state.estacionesSeleccionadas.contains(estacion.estacionId)

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("estacion_item_${estacion.estacionId}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEvent(CrearRutaUIEvent.ToggleEstacionSeleccionada(estacion.estacionId)) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = estacion.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(text = estacion.direccion, style = MaterialTheme.typography.bodyMedium)
                                }
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { onEvent(CrearRutaUIEvent.ToggleEstacionSeleccionada(estacion.estacionId)) },
                                    modifier = Modifier.testTag("checkbox_${estacion.estacionId}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CrearRutaBodyPreview() {
    MaterialTheme {
        CrearRutaBody(
            state = CrearRutaUIState(
                isLoading = false,
                estacionesDisponibles = listOf(
                    EstacionVentas(estacionId = 1, nombre = "Banca Norte", direccion = "Av. Principal", agenteId = 1),
                    EstacionVentas(estacionId = 2, nombre = "Banca Sur", direccion = "Calle 8", agenteId = 2)
                ),
                estacionesSeleccionadas = setOf(1)
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}