package ucne.edu.rocash.presentation.common

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta

@Composable
fun EstadoRutaChip(estado: EstadoRuta, modifier: Modifier = Modifier) {
    val (texto, contenedor, contenido) = when (estado) {
        EstadoRuta.PENDIENTE -> Triple(
            "Pendiente",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        EstadoRuta.EN_PROGRESO -> Triple(
            "En progreso",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        EstadoRuta.CERRADA -> Triple(
            "Cerrada",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(texto, style = MaterialTheme.typography.labelMedium) },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = contenedor,
            disabledLabelColor = contenido
        ),
        border = null,
        modifier = modifier.testTag("chip_estado_${estado.name}")
    )
}
