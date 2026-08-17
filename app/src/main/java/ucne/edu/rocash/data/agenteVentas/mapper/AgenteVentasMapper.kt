package ucne.edu.rocash.data.agenteVentas.mapper

import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasEntity
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

fun AgenteVentasEntity.toDomain(): AgenteVentas = AgenteVentas(
    agenteId = agenteId,
    nombre = nombre,
    telefono = telefono,
    deudaAcumulada = deudaAcumulada,
    estado = estado
)

fun AgenteVentas.toEntity(): AgenteVentasEntity = AgenteVentasEntity(
    agenteId = agenteId,
    nombre = nombre,
    telefono = telefono,
    deudaAcumulada = deudaAcumulada,
    estado = estado
)