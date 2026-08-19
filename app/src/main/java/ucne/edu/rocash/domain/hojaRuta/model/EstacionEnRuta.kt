package ucne.edu.rocash.domain.hojaRuta.model

import ucne.edu.rocash.domain.estacion.model.EstacionVentas
enum class EstadoVisitaEstacion { PENDIENTE, COMPLETADA, OMITIDA }

data class EstacionEnRuta(
    val estacion: EstacionVentas,
    val orden: Int = 0,
    val estado: EstadoVisitaEstacion = EstadoVisitaEstacion.PENDIENTE
) {
    val estacionId: Int get() = estacion.estacionId
    val nombre: String get() = estacion.nombre
    val direccion: String get() = estacion.direccion
}
