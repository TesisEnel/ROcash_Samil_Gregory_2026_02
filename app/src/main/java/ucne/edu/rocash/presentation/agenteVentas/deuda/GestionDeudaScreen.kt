package ucne.edu.rocash.presentation.agenteVentas.deuda

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda
import ucne.edu.rocash.presentation.common.aFechaLegible
import ucne.edu.rocash.presentation.common.aMoneda
import ucne.edu.rocash.ui.theme.coloresAccion

@Composable
fun GestionDeudaScreen(
    agenteId: Int,
    viewModel: GestionDeudaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(agenteId) {
        viewModel.onEvent(GestionDeudaUiEvent.Load(agenteId))
    }

    GestionDeudaBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionDeudaBody(
    state: GestionDeudaUiState,
    onEvent: (GestionDeudaUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            onEvent(GestionDeudaUiEvent.MensajeMostrado)
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            onEvent(GestionDeudaUiEvent.ErrorMostrado)
        }
    }

    if (state.mostrarDialogoSaldar) {
        AlertDialog(
            onDismissRequest = { onEvent(GestionDeudaUiEvent.CancelarSaldar) },
            title = { Text("¿Saldar toda la deuda?") },
            text = {
                Text(
                    "Se registrará un abono por ${state.deudaActual.aMoneda()} y la deuda " +
                            "de ${state.nombreAgente} quedará en cero."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { onEvent(GestionDeudaUiEvent.Saldar) },
                    modifier = Modifier.testTag("btn_confirmar_saldar")
                ) {
                    Text("Sí, saldar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(GestionDeudaUiEvent.CancelarSaldar) }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.nombreAgente.ifEmpty { "Deuda del agente" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("Abonos y saldo", style = MaterialTheme.typography.labelSmall)
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
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("loading")
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { TarjetaSaldo(state) }

                    if (state.tieneDeuda) {
                        item { FormularioAbono(state, onEvent) }
                    }

                    item {
                        Text(
                            text = "Historial de abonos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (!state.hayAbonos) {
                        item {
                            Text(
                                text = "Todavía no hay abonos registrados.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("empty_abonos")
                            )
                        }
                    } else {
                        items(items = state.abonos, key = { it.abonoId }) { abono ->
                            AbonoItem(abono)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaSaldo(state: GestionDeudaUiState) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (state.tieneDeuda) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_saldo")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (state.tieneDeuda) "Deuda pendiente" else "Sin deuda",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = state.deudaActual.aMoneda(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            if (state.totalAbonado > 0) {
                Text(
                    text = "Abonado hasta hoy: ${state.totalAbonado.aMoneda()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun FormularioAbono(
    state: GestionDeudaUiState,
    onEvent: (GestionDeudaUiEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = state.montoAbono,
            onValueChange = { onEvent(GestionDeudaUiEvent.MontoChanged(it)) },
            label = { Text("Monto del abono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = state.montoError != null,
            supportingText = state.montoError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_monto_abono")
        )

        OutlinedTextField(
            value = state.nota,
            onValueChange = { onEvent(GestionDeudaUiEvent.NotaChanged(it)) },
            label = { Text("Nota (opcional)") },
            minLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_nota_abono")
        )

        Button(
            colors = coloresAccion(),
            onClick = { onEvent(GestionDeudaUiEvent.Abonar) },
            enabled = state.puedeAbonar,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_abonar")
        ) {
            if (state.isProcesando) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Registrar abono")
            }
        }

        OutlinedButton(
            onClick = { onEvent(GestionDeudaUiEvent.PedirConfirmacionSaldar) },
            enabled = !state.isProcesando,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_saldar")
        ) {
            Text("Saldar toda la deuda")
        }
    }
}

@Composable
private fun AbonoItem(abono: AbonoDeuda) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = abono.monto.aMoneda(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = abono.fecha.aFechaLegible(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "${abono.deudaAntes.aMoneda()} → ${abono.deudaDespues.aMoneda()}",
                style = MaterialTheme.typography.bodyMedium
            )

            abono.nota?.let { nota ->
                Text(
                    text = nota,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GestionDeudaConDeudaPreview() {
    MaterialTheme {
        GestionDeudaBody(
            state = GestionDeudaUiState(
                isLoading = false,
                agenteId = 1,
                nombreAgente = "Ramón Peralta",
                deudaActual = 7500.0,
                tieneDeuda = true,
                totalAbonado = 2500.0,
                hayAbonos = true,
                abonos = listOf(
                    AbonoDeuda(
                        abonoId = 1,
                        agenteId = 1,
                        monto = 2500.0,
                        deudaAntes = 10000.0,
                        deudaDespues = 7500.0,
                        fecha = 1_770_000_000_000,
                        nota = "Entregó en la banca"
                    )
                )
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GestionDeudaSinDeudaPreview() {
    MaterialTheme {
        GestionDeudaBody(
            state = GestionDeudaUiState(
                isLoading = false,
                agenteId = 2,
                nombreAgente = "Yaneris Gómez",
                deudaActual = 0.0
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}
