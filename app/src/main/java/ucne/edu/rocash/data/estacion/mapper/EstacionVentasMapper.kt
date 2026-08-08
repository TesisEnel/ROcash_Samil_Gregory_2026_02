package ucne.edu.rocash.data.estacion.mapper

import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

fun EstacionVentasEntity.toDomain(): EstacionVentas {
    return EstacionVentas(
        id = this.id,
        hojaRutaId = hojaRutaId,
        nombre = this.nombre,
        direccion = this.direccion,
        agenteId = this.agenteId
    )
}

fun EstacionVentas.toEntity(): EstacionVentasEntity {
    return EstacionVentasEntity(
        id = this.id,
        hojaRutaId = hojaRutaId,
        nombre = this.nombre,
        direccion = this.direccion,
        agenteId = this.agenteId
    )
}