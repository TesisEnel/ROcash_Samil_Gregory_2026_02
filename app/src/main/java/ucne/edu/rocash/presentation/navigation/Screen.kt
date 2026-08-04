package ucne.edu.rocash.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRecolectorRoute

@Serializable
object HojaRutaCierreRoute

@Serializable
data class DetalleEstacionRoute(
    val estacionId: Int,
    val agenteId: Int,
    val nombreEstacion: String
)