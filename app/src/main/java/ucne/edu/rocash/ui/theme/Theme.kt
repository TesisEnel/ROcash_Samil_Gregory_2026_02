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
 * se veía igual en dos teléfonos y la marca no aparecía en ninguno.
 *
 * Los roles cargan significado, no decoración:
 *
 *  - `primary` es el verde del logo. Marca, dinero y estados completados: lo
 *    que la app INFORMA.
 *  - `tertiary` es el azul. Botones y FAB: lo que el usuario PUEDE TOCAR.
 *    Separarlos evita la ambigüedad de que un total y un botón compartan color.
 *  - `error` es ladrillo, y es el mismo color de las deudas. Si aparece rojo en
 *    pantalla siempre significa dinero pendiente.
 */
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
