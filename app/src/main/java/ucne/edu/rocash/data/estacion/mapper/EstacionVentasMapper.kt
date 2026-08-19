package ucne.edu.rocash.data.estacion.mapper

import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

fun EstacionVentasEntity.toDomain(): EstacionVentas = EstacionVentas(
    estacionId = estacionId,
    nombre = nombre,
    direccion = direccion,
    agenteId = agenteId,
    agenteId2 = agenteId2
)

fun EstacionVentas.toEntity(): EstacionVentasEntity = EstacionVentasEntity(
    estacionId = estacionId,
    nombre = nombre,
    direccion = direccion,
    agenteId = agenteId,
    agenteId2 = agenteId2
)
