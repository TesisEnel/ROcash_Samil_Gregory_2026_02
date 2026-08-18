package ucne.edu.rocash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Tema de RoCash.
 *
 * `dynamicColor` quedó fuera a propósito. Con el color dinámico de Android 12+
 * la app tomaba los colores del fondo de pantalla de cada usuario, así que no
 * se veía igual en dos teléfonos y no tenía identidad propia en ninguno. Una
 * herramienta que maneja el efectivo de terceros gana más siendo reconocible
 * que siendo camaleónica.
 *
 * El rojo está reservado: `error` es el mismo ladrillo que se usa para las
 * deudas. Si aparece color de alarma en pantalla, siempre significa que hay
 * dinero pendiente o algo que atender, nunca decoración.
 */
private val EsquemaClaro = lightColorScheme(
    primary = VerdeCuadre,
    onPrimary = Color.White,
    primaryContainer = VerdeContenedor,
    onPrimaryContainer = VerdeContenedorOscuro,

    secondary = AmbarRuta,
    onSecondary = Color.White,
    secondaryContainer = AmbarContenedor,
    onSecondaryContainer = AmbarContenedorOscuro,

    tertiary = VerdeCuadreClaro,
    onTertiary = VerdeContenedorOscuro,

    error = Ladrillo,
    onError = Color.White,
    errorContainer = LadrilloContenedor,
    onErrorContainer = LadrilloContenedorOscuro,

    background = FondoClaro,
    onBackground = TintaClara,
    surface = SuperficieClara,
    onSurface = TintaClara,
    surfaceVariant = SuperficieVarianteClara,
    onSurfaceVariant = TintaTenueClara,
    outline = Color(0xFF6F7B77)
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeCuadreClaro,
    onPrimary = VerdeContenedorOscuro,
    primaryContainer = VerdeContenedorOscuro,
    onPrimaryContainer = VerdeContenedor,

    secondary = AmbarRutaClaro,
    onSecondary = AmbarContenedorOscuro,
    secondaryContainer = AmbarContenedorOscuro,
    onSecondaryContainer = AmbarContenedor,

    tertiary = VerdeContenedor,
    onTertiary = VerdeContenedorOscuro,

    error = LadrilloClaro,
    onError = LadrilloContenedorOscuro,
    errorContainer = LadrilloContenedorOscuro,
    onErrorContainer = LadrilloContenedor,

    background = FondoOscuro,
    onBackground = TintaOscura,
    surface = SuperficieOscura,
    onSurface = TintaOscura,
    surfaceVariant = SuperficieVarianteOscura,
    onSurfaceVariant = TintaTenueOscura,
    outline = Color(0xFF89958F)
)

@Composable
fun ROcashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
