package ucne.edu.rocash.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object AuthRoute

@Serializable
object HomeRecolectorRoute

@Serializable
object HojaRutaCierreRoute

@Serializable
data class DetalleEstacionRoute(
    val estacionId: String,
    val agenteId: String,
    val nombreEstacion: String
)

@Serializable
object EstacionListRoute

@Serializable
object EstacionFormRoute

@Serializable
object CrearRutaRoute

@Serializable
object ListaRecolectoresRoute

@Serializable
object FormRecolectorRoute

@Serializable
object AgenteListRoute

@Serializable
object AgenteFormRoute