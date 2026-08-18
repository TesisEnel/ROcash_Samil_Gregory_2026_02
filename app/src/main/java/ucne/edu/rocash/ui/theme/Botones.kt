package ucne.edu.rocash.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Colores de los botones de acción: azul, no verde.
 *
 * El verde de la marca queda reservado para informar (totales, estados
 * completados, chips de ruta). Si un total recaudado y el botón que lo guarda
 * compartieran color, nada distinguiría lo que se lee de lo que se toca.
 *
 * Se aplica sobre `primary` en lugar de cambiar el rol del tema porque los
 * iconos, los montos y las marcas de completado deben seguir siendo verdes.
 */
@Composable
fun coloresAccion(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.tertiary,
    contentColor = MaterialTheme.colorScheme.onTertiary
)
