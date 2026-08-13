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
    val hojaRutaId: Int,
    val estacionId: Int,
    val agenteId: Int,
    val nombreEstacion: String
)

@Serializable
object EstacionListRoute

@Serializable
data class EstacionFormRoute(val estacionId: Int? = null)

@Serializable
object CrearRutaRoute

@Serializable
object ListaRecolectoresRoute

@Serializable
data class FormRecolectorRoute(val recolectorId: String? = null)

@Serializable
object AgenteListRoute

@Serializable
data class AgenteFormRoute(val agenteId: Int? = null)

@Serializable
object HistorialRutaRoute