package ucne.edu.rocash.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object AuthRoute

@Serializable
object SignUpRoute

@Serializable
object ProfileRoute

@Serializable
object HomeRecolectorRoute

@Serializable
object CrearRutaRoute
@Serializable
data class DetalleRutaRoute(val rutaId: Int)

@Serializable
data class CuadreEstacionRoute(
    val hojaRutaId: Int,
    val estacionId: Int,
    val agenteId: Int,
    val nombreEstacion: String
)

@Serializable
object HistorialRutaRoute

@Serializable
object EstacionListRoute

@Serializable
data class EstacionFormRoute(val estacionId: Int? = null)

@Serializable
object AgenteListRoute

@Serializable
data class AgenteFormRoute(val agenteId: Int? = null)

@Serializable
data class GestionDeudaRoute(val agenteId: Int)

@Serializable
data class EstacionDetalleRoute(val estacionId: Int)