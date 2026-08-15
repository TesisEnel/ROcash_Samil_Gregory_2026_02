package ucne.edu.rocash.presentation.hojaRuta.cuadre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.presentation.common.aMoneda

@Composable
fun CuadreScreen(
    hojaRutaId: Int,
    estacionId: Int,
    agenteId: Int,
    nombreEstacion: String,
    viewModel: CuadreViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(hojaRutaId, estacionId) {
        viewModel.onEvent(
            CuadreUiEvent.Load(hojaRutaId, estacionId, agenteId, nombreEstacion)
        )
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onNavigateBack()
    }

    CuadreBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuadreBody(
    state: CuadreUiState,
    onEvent: (CuadreUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            onEvent(CuadreUiEvent.ErrorMostrado)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.nombreEstacion.ifEmpty { "Cuadre de banca" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (state.isNew) "Nuevo cuadre"
                            else "Editando cuadre registrado",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(16.dp)
            ) {
                Button(
                    onClick = { onEvent(CuadreUiEvent.Save) },
                    enabled = state.puedeGuardar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_save_recoleccion")
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(if (state.isNew) "Guardar cuadre" else "Actualizar cuadre")
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
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("loading")
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.ventaBruta,
                        onValueChange = { onEvent(CuadreUiEvent.VentaBrutaChanged(it)) },
                        label = { Text("Venta bruta") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = state.ventaBrutaError != null,
                        supportingText = state.ventaBrutaError?.let { { Text(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_venta_bruta")
                    )

                    OutlinedTextField(
                        value = state.comisionCliente,
                        onValueChange = { onEvent(CuadreUiEvent.ComisionChanged(it)) },
                        label = { Text("Comisión del cliente") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = state.comisionError != null,
                        supportingText = state.comisionError?.let { { Text(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_comision_cliente")
                    )

                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_monto_esperado")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Monto esperado", style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = state.montoEsperado.aMoneda(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = state.montoRecolectado,
                        onValueChange = { onEvent(CuadreUiEvent.MontoRecolectadoChanged(it)) },
                        label = { Text("Monto efectivo recolectado") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = state.montoRecolectadoError != null,
                        supportingText = state.montoRecolectadoError?.let { { Text(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_monto_recolectado")
                    )

                    if (state.deudaGenerada > 0) {
                        Text(
                            text = "Deuda al agente: ${state.deudaGenerada.aMoneda()}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("text_deuda_generada")
                        )
                        if (state.agenteId2 != null) {
                            Text(
                                text = "Se reparte en partes iguales entre los dos agentes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = state.notaIncidencia,
                        onValueChange = { onEvent(CuadreUiEvent.NotaChanged(it)) },
                        label = { Text("Nota o incidencia (opcional)") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_nota")
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CuadreBodyPreview() {
    MaterialTheme {
        CuadreBody(
            state = CuadreUiState(
                isLoading = false,
                nombreEstacion = "Banca Principal",
                ventaBruta = "5000",
                comisionCliente = "1000",
                montoEsperado = 4000.0,
                montoRecolectado = "4000"
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CuadreBodyConDeudaPreview() {
    MaterialTheme {
        CuadreBody(
            state = CuadreUiState(
                isLoading = false,
                isNew = false,
                nombreEstacion = "Banca Sur",
                agenteId2 = 4,
                ventaBruta = "10000",
                comisionCliente = "2000",
                montoEsperado = 8000.0,
                montoRecolectado = "5000",
                deudaGenerada = 3000.0
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}