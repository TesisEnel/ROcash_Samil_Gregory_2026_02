package ucne.edu.rocash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = VerdeROcash,
    onPrimary = Color.White,
    primaryContainer = VerdeContenedor,
    onPrimaryContainer = VerdeContenedorOscuro,

    secondary = VerdeROcash,
    onSecondary = Color.White,
    secondaryContainer = VerdeContenedor,
    onSecondaryContainer = VerdeContenedorOscuro,

    tertiary = AzulAccion,
    onTertiary = Color.White,
    tertiaryContainer = AzulContenedor,
    onTertiaryContainer = AzulContenedorOscuro,

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
    outline = Color(0xFF737B70)
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeROcashClaro,
    onPrimary = VerdeContenedorOscuro,
    primaryContainer = VerdeContenedorOscuro,
    onPrimaryContainer = VerdeContenedor,

    secondary = VerdeROcashClaro,
    onSecondary = VerdeContenedorOscuro,
    secondaryContainer = VerdeContenedorOscuro,
    onSecondaryContainer = VerdeContenedor,

    tertiary = AzulAccionClaro,
    onTertiary = AzulContenedorOscuro,
    tertiaryContainer = AzulContenedorOscuro,
    onTertiaryContainer = AzulContenedor,

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
    outline = Color(0xFF8C948A)
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
