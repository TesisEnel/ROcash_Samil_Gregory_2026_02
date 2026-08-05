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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRutaScreen(
    viewModel: CrearRutaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateBack() // Regresa al Home cuando se crea exitosamente
        }
    }

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
                    onClick = { viewModel.processIntent(CrearRutaUIEvent.GenerarHojaRuta) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !state.isLoading && state.estacionesSeleccionadas.isNotEmpty()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Generar Ruta con ${state.estacionesSeleccionadas.size} Bancas")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading && state.estacionesDisponibles.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.estacionesDisponibles.isEmpty()) {
                Text(
                    text = "No hay estaciones registradas en el sistema.",
                    modifier = Modifier.align(Alignment.Center)
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
                                text = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    items(state.estacionesDisponibles) { estacion ->
                        val isSelected = state.estacionesSeleccionadas.contains(estacion.id)
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.processIntent(CrearRutaUIEvent.ToggleEstacionSeleccionada(estacion.id)) }
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
                                    onCheckedChange = { viewModel.processIntent(CrearRutaUIEvent.ToggleEstacionSeleccionada(estacion.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}