package ucne.edu.rocash.presentation.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DetalleEstacionScreen(
    hojaRutaId: Int,
    estacionId: Int,
    agenteId: Int,
    nombreEstacion: String,
    viewModel: DetalleViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(DetalleUiEvent.Load(hojaRutaId, estacionId, agenteId, nombreEstacion))
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onNavigateBack()
    }

    DetalleEstacionBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEstacionBody(
    state: DetalleUiState,
    onEvent: (DetalleUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.nombreEstacion.ifEmpty { "Detalle de Estación" }) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("error_message_general")
                )
            }

            OutlinedTextField(
                value = state.ventaBruta,
                onValueChange = { onEvent(DetalleUiEvent.VentaBrutaChanged(it)) },
                label = { Text("Venta Bruta ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().testTag("input_venta_bruta"),
                isError = state.ventaBrutaError != null,
                supportingText = state.ventaBrutaError?.let { { Text(it) } }
            )

            OutlinedTextField(
                value = state.comisionCliente,
                onValueChange = { onEvent(DetalleUiEvent.ComisionChanged(it)) },
                label = { Text("Comisión del Cliente ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().testTag("input_comision_cliente"),
                isError = state.comisionError != null,
                supportingText = state.comisionError?.let { { Text(it) } }
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth().testTag("card_monto_esperado"),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Monto Esperado", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "$${state.montoEsperado}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedTextField(
                value = state.montoRecolectado,
                onValueChange = { onEvent(DetalleUiEvent.MontoRecolectadoChanged(it)) },
                label = { Text("Monto Efectivo Recolectado ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().testTag("input_monto_recolectado"),
                isError = state.montoRecolectadoError != null,
                supportingText = state.montoRecolectadoError?.let { { Text(it) } }
            )

            if (state.deudaGenerada > 0) {
                Text(
                    text = "Deuda al Agente: $${state.deudaGenerada}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("text_deuda_generada")
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onEvent(DetalleUiEvent.Save) },
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("btn_save_recoleccion"),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Guardar Recolección")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetalleEstacionBodyPreview() {
    MaterialTheme {
        DetalleEstacionBody(
            state = DetalleUiState(
                nombreEstacion = "Banca Principal",
                ventaBruta = "5000.0",
                comisionCliente = "1000.0",
                montoEsperado = 4000.0,
                montoRecolectado = "4000.0",
                deudaGenerada = 0.0
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetalleEstacionBodyDeudaPreview() {
    MaterialTheme {
        DetalleEstacionBody(
            state = DetalleUiState(
                nombreEstacion = "Banca Sur",
                ventaBruta = "10000.0",
                comisionCliente = "2000.0",
                montoEsperado = 8000.0,
                montoRecolectado = "5000.0",
                deudaGenerada = 3000.0,
                montoRecolectadoError = "Monto incompleto detectado"
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}