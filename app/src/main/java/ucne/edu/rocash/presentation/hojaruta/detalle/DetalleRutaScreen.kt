package ucne.edu.rocash.presentation.hojaRuta.detalle

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.hojaRuta.model.EstacionEnRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta
import ucne.edu.rocash.presentation.common.EstadoRutaChip
import ucne.edu.rocash.presentation.common.aFechaLegible
import ucne.edu.rocash.presentation.common.Confirmacion
import ucne.edu.rocash.presentation.common.ConfirmacionOverlay
import ucne.edu.rocash.presentation.common.PesoConfirmacion
import ucne.edu.rocash.presentation.common.aMoneda

@Composable
fun DetalleRutaScreen(
    rutaId: Int,
    viewModel: DetalleRutaViewModel = hiltViewModel(),
    onNavigateToCuadre: (rutaId: Int, estacionId: Int, agenteId: Int, nombre: String) -> Unit,
    onRutaCerrada: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(rutaId) {
        viewModel.onEvent(DetalleRutaUiEvent.Load(rutaId))
    }

    var confirmacion by remember { mutableStateOf<Confirmacion?>(null) }

    LaunchedEffect(state.cierreCompletado) {
        if (state.cierreCompletado && confirmacion == null) {
            confirmacion = Confirmacion(
                titulo = "Ruta cerrada",
                detalle = "${state.resumen.cantidadRegistros} bancas cobradas",
                monto = state.resumen.totalRecaudado,
                peso = PesoConfirmacion.Cierre
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DetalleRutaBody(
            state = state,
            onEvent = viewModel::onEvent,
            onNavigateToCuadre = onNavigateToCuadre,
            onNavigateBack = onNavigateBack
        )

        ConfirmacionOverlay(confirmacion = confirmacion) { onRutaCerrada() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRutaBody(
    state: DetalleRutaUiState,
    onEvent: (DetalleRutaUiEvent) -> Unit,
    onNavigateToCuadre: (rutaId: Int, estacionId: Int, agenteId: Int, nombre: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            onEvent(DetalleRutaUiEvent.ErrorMostrado)
        }
    }

    if (state.mostrarDialogoCierre) {
        ConfirmarCierreDialog(
            resumen = state.resumen,
            onConfirmar = { onEvent(DetalleRutaUiEvent.ConfirmarCierre) },
            onCancelar = { onEvent(DetalleRutaUiEvent.CancelarCierre) }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.ruta?.let { "Ruta #${it.id}" } ?: "Hoja de ruta",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    state.ruta?.let { EstadoRutaChip(estado = it.estado) }
                }
            )
        },
        bottomBar = {
            if (state.mostrarAccionCierre) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Button(
                        onClick = { onEvent(DetalleRutaUiEvent.PedirConfirmacionCierre) },
                        enabled = state.puedeCerrarse,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_cerrar_ruta")
                    ) {
                        when {
                            state.isCerrando -> CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            state.hayEstacionesPendientes -> Text(
                                "Faltan ${state.estacionesPendientes} bancas por cuadrar"
                            )
                            else -> Text("Cerrar hoja de ruta")
                        }
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

                state.noEncontrada || state.ruta == null -> Text(
                    text = "Esta hoja de ruta ya no existe.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .testTag("ruta_no_encontrada")
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        ResumenParcialCard(
                            ruta = state.ruta,
                            resumen = state.resumen
                        )
                    }

                    item {
                        Text(
                            text = "Estaciones (${state.ruta.cantidadEstaciones})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }

                    items(
                        items = state.ruta.estaciones,
                        key = { it.estacionId }
                    ) { item ->
                        EstacionDeRutaItem(
                            item = item,
                            rutaCerrada = state.rutaEstaCerrada,
                            onClick = {
                                onNavigateToCuadre(
                                    state.ruta.id,
                                    item.estacionId,
                                    item.estacion.agenteId,
                                    item.nombre
                                )
                            },
                            onOmitir = {
                                onEvent(DetalleRutaUiEvent.OmitirEstacion(item.estacionId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenParcialCard(
    ruta: HojaRuta,
    resumen: ResumenRecoleccionRuta,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_resumen_parcial"),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Cuadre acumulado",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = resumen.totalRecaudado.aMoneda(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Abierta el ${ruta.fechaCreacion.aFechaLegible()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            FilaResumen("Venta bruta", resumen.totalVentaBruta.aMoneda())
            FilaResumen("Comisión clientes", resumen.totalComisionClientes.aMoneda())
            FilaResumen(
                etiqueta = "Deudas generadas",
                valor = resumen.totalDeudas.aMoneda(),
                colorValor = if (resumen.totalDeudas > 0) MaterialTheme.colorScheme.error
                else Color.Unspecified
            )
            FilaResumen(
                etiqueta = "Bancas cuadradas",
                valor = "${ruta.estacionesCuadradas} / ${ruta.cantidadEstaciones}"
            )
        }
    }
}

@Composable
private fun FilaResumen(
    etiqueta: String,
    valor: String,
    colorValor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = etiqueta, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorValor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EstacionDeRutaItem(
    item: EstacionEnRuta,
    rutaCerrada: Boolean,
    onClick: () -> Unit,
    onOmitir: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuAbierto by remember { mutableStateOf(false) }

    val (icono, tinte) = when (item.estado) {
        EstadoVisitaEstacion.COMPLETADA ->
            Icons.Default.CheckCircle to MaterialTheme.colorScheme.primary
        EstadoVisitaEstacion.OMITIDA ->
            Icons.Default.RemoveCircle to MaterialTheme.colorScheme.error
        EstadoVisitaEstacion.PENDIENTE ->
            Icons.Default.RadioButtonUnchecked to MaterialTheme.colorScheme.outline
    }

    ElevatedCard(
        onClick = onClick,
        enabled = !rutaCerrada,
        modifier = modifier
            .fillMaxWidth()
            .testTag("estacion_ruta_${item.estacionId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = item.estado.name,
                tint = tinte,
                modifier = Modifier.size(28.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!rutaCerrada && item.estado == EstadoVisitaEstacion.PENDIENTE) {
                Box {
                    IconButton(
                        onClick = { menuAbierto = true },
                        modifier = Modifier.testTag("menu_estacion_${item.estacionId}")
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Marcar como no visitada") },
                            onClick = {
                                menuAbierto = false
                                onOmitir()
                            }
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ConfirmarCierreDialog(
    resumen: ResumenRecoleccionRuta,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Cerrar hoja de ruta") },
        text = {
            Column {
                Text("Una vez cerrada, la ruta pasa al historial y no admite más cuadres.")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                FilaResumen("Recaudado", resumen.totalRecaudado.aMoneda())
                FilaResumen("Deudas", resumen.totalDeudas.aMoneda())
                FilaResumen("Cuadres registrados", resumen.cantidadRegistros.toString())
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmar,
                modifier = Modifier.testTag("btn_confirmar_cierre")
            ) {
                Text("Cerrar ruta")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DetalleRutaBodyPreview() {
    fun banca(id: Int, nombre: String, estado: EstadoVisitaEstacion) = EstacionEnRuta(
        estacion = EstacionVentas(id, nombre, "Calle $id, SFM", agenteId = 1),
        orden = id,
        estado = estado
    )

    MaterialTheme {
        DetalleRutaBody(
            state = DetalleRutaUiState(
                rutaId = 7,
                isLoading = false,
                estacionesPendientes = 1,
                hayEstacionesPendientes = true,
                rutaEstaCerrada = false,
                mostrarAccionCierre = true,
                puedeCerrarse = false,
                ruta = HojaRuta(
                    id = 7,
                    recolectorId = "uid",
                    estado = EstadoRuta.EN_PROGRESO,
                    estaciones = listOf(
                        banca(1, "Banca Norte", EstadoVisitaEstacion.COMPLETADA),
                        banca(2, "Banca Sur", EstadoVisitaEstacion.OMITIDA),
                        banca(3, "Banca Central", EstadoVisitaEstacion.PENDIENTE)
                    )
                ),
                resumen = ResumenRecoleccionRuta(
                    totalVentaBruta = 45_000.0,
                    totalComisionClientes = 9_000.0,
                    totalRecaudado = 33_500.0,
                    totalDeudas = 2_500.0,
                    cantidadRegistros = 1
                )
            ),
            onEvent = {},
            onNavigateToCuadre = { _, _, _, _ -> },
            onNavigateBack = {}
        )
    }
}