package ucne.edu.rocash.presentation.estacion.form

import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.ui.theme.coloresAccion

@Composable
fun EstacionFormScreen(
    viewModel: EstacionFormViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onNavigateBack()
    }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onNavigateBack()
    }

    EstacionFormBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstacionFormBody(
    state: EstacionFormUiState,
    onEvent: (EstacionFormUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            onEvent(EstacionFormUiEvent.ErrorMostrado)
        }
    }

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (state.isNew) "Nueva Estación" else "Editar Estación") },
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
            OutlinedTextField(
                value = state.nombre,
                onValueChange = { onEvent(EstacionFormUiEvent.NombreChanged(it)) },
                label = { Text("Nombre de la Banca") },
                modifier = Modifier.fillMaxWidth().testTag("input_nombre_estacion"),
                singleLine = true,
                isError = state.nombreError != null,
                supportingText = state.nombreError?.let { { Text(it) } }
            )

            OutlinedTextField(
                value = state.direccion,
                onValueChange = { onEvent(EstacionFormUiEvent.DireccionChanged(it)) },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth().testTag("input_direccion_estacion"),
                isError = state.direccionError != null,
                supportingText = state.direccionError?.let { { Text(it) } }
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
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("dropdown_agente"),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    isError = state.agenteError != null,
                    supportingText = state.agenteError?.let { { Text(it) } }
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
                                    onEvent(EstacionFormUiEvent.AgenteSeleccionado(agente.agenteId, agente.nombre))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                colors = coloresAccion(),
                onClick = { onEvent(EstacionFormUiEvent.Save) },
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("btn_save_estacion"),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Guardar Estación")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EstacionFormBodyPreview() {
    MaterialTheme {
        EstacionFormBody(
            state = EstacionFormUiState(
                isNew = true,
                nombre = "",
                direccion = ""
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EstacionFormBodyErrorPreview() {
    MaterialTheme {
        EstacionFormBody(
            state = EstacionFormUiState(
                isNew = false,
                nombre = "A",
                direccion = "",
                nombreError = "El nombre debe tener al menos 3 caracteres",
                agenteError = "Debe seleccionar un agente"
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}