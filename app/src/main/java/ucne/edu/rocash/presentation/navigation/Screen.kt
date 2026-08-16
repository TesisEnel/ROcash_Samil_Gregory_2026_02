package ucne.edu.rocash.presentation.navigation

import kotlinx.serialization.Serializable

// ---------- Autenticación ----------

@Serializable
object AuthRoute

@Serializable
object SignUpRoute

@Serializable
object ProfileRoute

// ---------- Hoja de ruta ----------

@Serializable
object HomeRecolectorRoute


@Serializable
object CrearRutaRoute

@Serializable
object HistorialRutaRoute

@Serializable
object CrearRutaRoute

/** Ruta con sus estaciones. Desde aqui se cuadra y se cierra. */
@Serializable
data class DetalleRutaRoute(val rutaId: Int)

/**
 * Cuadre de una banca dentro de una ruta.
 *
 * Antes se llamaba DetalleEstacionRoute. `nombreEstacion` viaja como argumento
 * solo para pintar el titulo sin esperar a la base de datos.
 */
@Serializable
data class CuadreEstacionRoute(
    val hojaRutaId: Int,
    val estacionId: Int,
    val agenteId: Int,
    val nombreEstacion: String
)


@Serializable
object HistorialRutaRoute

// ---------- Catálogos ----------

@Serializable
object EstacionListRoute

@Serializable
data class EstacionFormRoute(val estacionId: Int? = null)

@Serializable
object ListaRecolectoresRoute

@Serializable
data class FormRecolectorRoute(val recolectorId: String? = null)


@Serializable
object AgenteListRoute

// HojaRutaCierreRoute se elimino: el cierre no es una pantalla aparte, es una
// accion dentro de DetalleRutaRoute. En el NavHost estaba declarada con el
// cuerpo vacio `{ // TODO }`, asi que navegar hacia ella mostraba una pantalla
// en blanco.
