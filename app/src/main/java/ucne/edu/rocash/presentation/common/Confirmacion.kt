package ucne.edu.rocash.presentation.common

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ucne.edu.rocash.ui.theme.VerdeROcashClaro
import ucne.edu.rocash.ui.theme.VerdeSello

/**
 * Cuánto pesa una confirmación.
 *
 * Deliberadamente no son iguales. Si las tres celebraran lo mismo, ninguna
 * significaría nada. El cierre de ruta es el final de la jornada del cobrador
 * y es el único que se toma su tiempo.
 */
enum class PesoConfirmacion(val duracionMs: Int) {
    /** Guardaste algo. Un parpadeo y sigue. */
    Ligera(750),

    /** Cuadraste una banca. Como sellar un recibo. */
    Sello(1_100),

    /** Cerraste la hoja de ruta. Se cuenta el dinero. */
    Cierre(2_000)
}

data class Confirmacion(
    val titulo: String,
    val detalle: String? = null,
    /** Si viene, se cuenta hacia arriba en vez de aparecer de golpe. */
    val monto: Double? = null,
    val peso: PesoConfirmacion = PesoConfirmacion.Ligera
)

/**
 * Overlay de confirmación. Aparece cuando [confirmacion] deja de ser null y
 * llama a [onTerminado] al acabar, que es donde va la navegación.
 *
 * Respeta la configuración de accesibilidad: si el usuario desactivó las
 * animaciones del sistema —común en equipos de gama baja, y los cobradores
 * probablemente los tengan— el overlay se salta por completo y navega directo.
 * La app no lo hace esperar por una animación que pidió no ver.
 */
@Composable
fun ConfirmacionOverlay(
    confirmacion: Confirmacion?,
    onTerminado: () -> Unit
) {
    val contexto = LocalContext.current
    val terminar by rememberUpdatedState(onTerminado)

    val animacionesActivas = remember(contexto) {
        Settings.Global.getFloat(
            contexto.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) != 0f
    }

    if (confirmacion == null) return

    if (!animacionesActivas) {
        LaunchedEffect(confirmacion) { terminar() }
        return
    }

    val progresoMarca = remember(confirmacion) { Animatable(0f) }
    val escala = remember(confirmacion) { Animatable(0.6f) }
    var montoMostrado by remember(confirmacion) { mutableFloatStateOf(0f) }

    LaunchedEffect(confirmacion) {
        val peso = confirmacion.peso

        // El sello llega con rebote; los otros dos entran sin dramatismo.
        escala.animateTo(
            targetValue = 1f,
            animationSpec = if (peso == PesoConfirmacion.Sello) {
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            } else {
                spring(stiffness = Spring.StiffnessMediumLow)
            }
        )

        progresoMarca.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = (peso.duracionMs * 0.45f).toInt(),
                easing = LinearEasing
            )
        )

        // Contar el dinero es el gesto propio del oficio.
        val total = confirmacion.monto
        if (total != null) {
            val contador = Animatable(0f)
            contador.animateTo(
                targetValue = total.toFloat(),
                animationSpec = tween(
                    durationMillis = (peso.duracionMs * 0.5f).toInt(),
                    easing = LinearEasing
                )
            ) {
                montoMostrado = value
            }
            montoMostrado = total.toFloat()
        }

        delay(peso.duracionMs / 4L)
        terminar()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            MarcaDeVisto(
                progreso = progresoMarca.value,
                escala = escala.value,
                grande = confirmacion.peso == PesoConfirmacion.Cierre
            )

            Text(
                text = confirmacion.titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                textAlign = TextAlign.Center
            )

            if (confirmacion.monto != null) {
                Text(
                    text = montoMostrado.toDouble().aMoneda(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = VerdeROcashClaro,
                    textAlign = TextAlign.Center
                )
            }

            confirmacion.detalle?.let { detalle ->
                Text(
                    text = detalle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * El visto se dibuja trazo a trazo en vez de aparecer hecho.
 *
 * [PathMeasure.getSegment] recorta el camino completo a la fracción ya
 * "escrita", así que el trazo avanza como lo haría una mano.
 */
@Composable
private fun MarcaDeVisto(
    progreso: Float,
    escala: Float,
    grande: Boolean
) {
    val lado = if (grande) 96.dp else 64.dp
    val colorAnillo = VerdeSello
    val colorMarca = MaterialTheme.colorScheme.inverseOnSurface

    val caminoCompleto = remember { Path() }
    val segmento = remember { Path() }
    val medidor = remember { PathMeasure() }

    Canvas(modifier = Modifier.size(lado * escala)) {
        val radio = size.minDimension / 2f
        val grosorAnillo = size.minDimension * 0.09f

        drawCircle(
            color = colorAnillo,
            radius = radio - grosorAnillo / 2f
        )

        caminoCompleto.reset()
        caminoCompleto.moveTo(size.width * 0.28f, size.height * 0.52f)
        caminoCompleto.lineTo(size.width * 0.44f, size.height * 0.68f)
        caminoCompleto.lineTo(size.width * 0.73f, size.height * 0.35f)

        medidor.setPath(caminoCompleto, false)

        segmento.reset()
        medidor.getSegment(
            startDistance = 0f,
            stopDistance = medidor.length * progreso.coerceIn(0f, 1f),
            destination = segmento,
            startWithMoveTo = true
        )

        drawPath(
            path = segmento,
            color = colorMarca,
            style = Stroke(
                width = size.minDimension * 0.11f,
                cap = StrokeCap.Round
            )
        )
    }
}
