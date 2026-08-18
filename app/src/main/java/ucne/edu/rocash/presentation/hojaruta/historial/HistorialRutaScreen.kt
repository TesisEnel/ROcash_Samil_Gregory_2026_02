package ucne.edu.rocash.presentation.hojaRuta.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.presentation.common.aFechaLegible
import ucne.edu.rocash.presentation.common.aMoneda

@Composable
fun HistorialRutasScreen(
    viewModel: HistorialRutasViewModel = hiltViewModel(),
    onNavigateToDetalleRuta: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HistorialRutasBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToDetalleRuta = onNavigateToDetalleRuta,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialRutasBody(
    state: HistorialRutasUiState,
    onEvent: (HistorialRutasUiEvent) -> Unit,
    onNavigateToDetalleRuta: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            onEvent(HistorialRutasUiEvent.ErrorMostrado)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Historial de rutas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
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

                state.sinSesion -> Text(
                    text = "No hay una sesión activa.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .testTag("sin_sesion")
                )

                !state.hayRutas -> Text(
                    text = "Todavía no has cerrado ninguna ruta.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .testTag("historial_vacio")
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${state.cantidadRutas} rutas cerradas · " +
                                    state.totalRecaudadoHistorico.aMoneda(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(
                        items = state.rutas,
                        key = { it.id }
                    ) { ruta ->
                        RutaHistorialItem(
                            ruta = ruta,
                            onClick = { onNavigateToDetalleRuta(ruta.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RutaHistorialItem(
    ruta: HojaRuta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("ruta_historial_${ruta.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ruta #${ruta.id}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = (ruta.fechaCierre ?: ruta.fechaCreacion).aFechaLegible(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            FilaMonto(
                etiqueta = "Recaudado",
                valor = ruta.totalRecaudado.aMoneda(),
                destacado = true
            )
            FilaMonto("Venta bruta", ruta.totalVentaBruta.aMoneda())
            FilaMonto("Comisión clientes", ruta.totalComisionClientes.aMoneda())
            FilaMonto(
                etiqueta = "Deudas",
                valor = ruta.totalDeudas.aMoneda(),
                color = if (ruta.totalDeudas > 0) MaterialTheme.colorScheme.error
                else Color.Unspecified
            )
        }
    }
}

@Composable
private fun FilaMonto(
    etiqueta: String,
    valor: String,
    destacado: Boolean = false,
    color: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (destacado) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
            color = when {
                color != Color.Unspecified -> color
                destacado -> MaterialTheme.colorScheme.primary
                else -> Color.Unspecified
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistorialRutasBodyPreview() {
    MaterialTheme {
        HistorialRutasBody(
            state = HistorialRutasReducer.conHistorial(
                estado = HistorialRutasUiState(),
                totalRecaudadoHistorico = 88_400.0,
                rutas = listOf(
                    HojaRuta(
                        id = 5,
                        recolectorId = "uid",
                        estado = EstadoRuta.CERRADA,
                        fechaCierre = System.currentTimeMillis(),
                        totalVentaBruta = 62_000.0,
                        totalComisionClientes = 12_400.0,
                        totalRecaudado = 47_600.0,
                        totalDeudas = 2_000.0
                    ),
                    HojaRuta(
                        id = 4,
                        recolectorId = "uid",
                        estado = EstadoRuta.CERRADA,
                        fechaCierre = System.currentTimeMillis() - 86_400_000,
                        totalVentaBruta = 51_000.0,
                        totalComisionClientes = 10_200.0,
                        totalRecaudado = 40_800.0
                    )
                )
            ),
            onEvent = {},
            onNavigateToDetalleRuta = {},
            onNavigateBack = {}
        )
    }
}