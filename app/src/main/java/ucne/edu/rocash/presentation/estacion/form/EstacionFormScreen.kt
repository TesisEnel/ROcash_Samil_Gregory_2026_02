package ucne.edu.rocash.presentation.estacion.form

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstacionFormScreen(
    viewModel: EstacionFormViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.processIntent(EstacionFormUiEvent.ResetSuccessState)
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == null) "Nueva Estación (Banca)" else "Editar Estación") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = state.nombre,
                onValueChange = { viewModel.processIntent(EstacionFormUiEvent.OnNombreChange(it)) },
                label = { Text("Nombre de la Banca") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.direccion,
                onValueChange = { viewModel.processIntent(EstacionFormUiEvent.OnDireccionChange(it)) },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.agenteNombreSeleccionado.ifEmpty { "Seleccione un agente" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Agente Asignado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (state.agentesDisponibles.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay agentes activos") },
                            onClick = { expanded = false }
                        )
                    } else {
                        state.agentesDisponibles.forEach { agente ->
                            DropdownMenuItem(
                                text = { Text(agente.nombre) },
                                onClick = {
                                    viewModel.processIntent(EstacionFormUiEvent.OnAgenteSeleccionado(agente.id, agente.nombre))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.processIntent(EstacionFormUiEvent.GuardarEstacion) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(if (state.id == null) "Guardar Estación" else "Actualizar Estación")
                }
            }
        }
    }
}