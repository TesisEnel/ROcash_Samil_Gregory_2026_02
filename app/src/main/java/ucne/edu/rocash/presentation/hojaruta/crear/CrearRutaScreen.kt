package ucne.edu.rocash.presentation.hojaRuta.crear

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

@Composable
fun CrearRutaScreen(
    viewModel: CrearRutaViewModel = hiltViewModel(),
    onRutaCreada: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.rutaCreadaId) {
        state.rutaCreadaId?.let(onRutaCreada)
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
    state: CrearRutaUiState,
    onEvent: (CrearRutaUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            onEvent(CrearRutaUiEvent.ErrorMostrado)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Armar Hoja de Ruta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (state.cantidadSeleccionada > 0) {
                        TextButton(
                            onClick = { onEvent(CrearRutaUiEvent.LimpiarSeleccion) },
                            modifier = Modifier.testTag("btn_limpiar_seleccion")
                        ) {
                            Text("Limpiar")
                        }
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
                    onClick = { onEvent(CrearRutaUiEvent.GenerarHojaRuta) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_generar_ruta"),
                    enabled = state.puedeGuardar
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Generar ruta con ${state.cantidadSeleccionada} bancas")
                    }
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

                state.estacionesDisponibles.isEmpty() -> Text(
                    text = "No hay estaciones registradas en el sistema.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .testTag("empty_message")
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("lista_estaciones"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.estacionesDisponibles,
                        key = { it.estacionId }
                    ) { estacion ->
                        EstacionSeleccionableItem(
                            estacion = estacion,
                            seleccionada = state.estaSeleccionada(estacion.estacionId),
                            comprometida = state.estaComprometida(estacion.estacionId),
                            onToggle = {
                                onEvent(CrearRutaUiEvent.ToggleEstacion(estacion.estacionId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EstacionSeleccionableItem(
    estacion: EstacionVentas,
    seleccionada: Boolean,
    comprometida: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contenedor = when {
        comprometida -> MaterialTheme.colorScheme.surfaceVariant
        seleccionada -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    ElevatedCard(
        onClick = onToggle,
        enabled = !comprometida,
        colors = CardDefaults.elevatedCardColors(containerColor = contenedor),
        modifier = modifier
            .fillMaxWidth()
            .testTag("estacion_item_${estacion.estacionId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = estacion.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = estacion.direccion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (comprometida) {
                    Text(
                        text = "Ya asignada a una ruta abierta",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag("tag_comprometida_${estacion.estacionId}")
                    )
                }
            }

            Checkbox(
                checked = seleccionada,
                onCheckedChange = { onToggle() },
                enabled = !comprometida,
                modifier = Modifier.testTag("checkbox_${estacion.estacionId}")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CrearRutaBodyPreview() {
    MaterialTheme {
        CrearRutaBody(
            state = CrearRutaUiState(
                isLoading = false,
                estacionesDisponibles = listOf(
                    EstacionVentas(1, "Banca Norte", "Av. Principal 12", agenteId = 1),
                    EstacionVentas(2, "Banca Sur", "Calle 8 esq. Duarte", agenteId = 2),
                    EstacionVentas(3, "Banca Central", "Parque Duarte", agenteId = 1)
                ),
                estacionesSeleccionadas = setOf(1),
                estacionesComprometidas = setOf(3)
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CrearRutaBodyVaciaPreview() {
    MaterialTheme {
        CrearRutaBody(
            state = CrearRutaUiState(isLoading = false),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}