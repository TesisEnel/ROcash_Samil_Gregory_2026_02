package ucne.edu.rocash.presentation.recolector.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormRecolectorScreen(
    recolectorId: String? = null,
    viewModel: FormRecolectorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(recolectorId) {
        viewModel.processIntent(FormRecolectorUiEvent.Inicializar(recolectorId))
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.processIntent(FormRecolectorUiEvent.ResetSuccessState)
            onNavigateBack()
        }
    }
    val titulo = if (state.recolectorId == null) "Nuevo Recolector" else "Editar Recolector"
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Recolector") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar")
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
                onValueChange = { viewModel.processIntent(FormRecolectorUiEvent.OnNombreChange(it)) },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.telefono,
                onValueChange = { viewModel.processIntent(FormRecolectorUiEvent.OnTelefonoChange(it)) },
                label = { Text("Número de Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            OutlinedTextField(
                value = state.cedula,
                onValueChange = { viewModel.processIntent(FormRecolectorUiEvent.OnCedulaChange(it)) },
                label = { Text("Cedula") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.processIntent(FormRecolectorUiEvent.GuardarRecolector) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = state.nombre.isNotBlank() && state.telefono.isNotBlank() && !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Guardar Recolector")
                }
            }
        }
    }
}