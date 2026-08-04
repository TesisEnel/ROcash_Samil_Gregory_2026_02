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