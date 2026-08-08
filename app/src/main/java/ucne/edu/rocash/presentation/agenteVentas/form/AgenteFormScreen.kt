package ucne.edu.rocash.presentation.agenteVentas.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenteFormScreen(
    viewModel: AgenteFormViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.processIntent(AgenteFormUiEvent.ResetSuccessState)
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Agente") },
                navigationIcon = { IconButton (onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar") } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (state.errorMessage != null) Text(text = state.errorMessage!!, color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = state.nombre,
                onValueChange = { viewModel.processIntent(AgenteFormUiEvent.OnNombreChange(it)) },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.telefono,
                onValueChange = { viewModel.processIntent(AgenteFormUiEvent.OnTelefonoChange(it)) },
                label = { Text("Número de Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.processIntent(AgenteFormUiEvent.GuardarAgente) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = state.nombre.isNotBlank() && state.telefono.isNotBlank() && !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("Guardar Agente")
            }
        }
    }
}