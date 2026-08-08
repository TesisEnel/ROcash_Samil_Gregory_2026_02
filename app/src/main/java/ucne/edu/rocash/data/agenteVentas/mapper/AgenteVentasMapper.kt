package ucne.edu.rocash.data.agenteVentas.mapper

import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasEntity
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

fun AgenteVentas.toEntity(): AgenteVentasEntity {
    return AgenteVentasEntity(
        id = this.id,
        nombre = this.nombre,
        telefono = this.telefono,
        deudaAcumulada = this.deudaAcumulada,
        estado = this.estado
    )
}

fun AgenteVentasEntity.toDomain(): AgenteVentas {
    return AgenteVentas(
        id = this.id,
        nombre = this.nombre,
        telefono = this.telefono,
        deudaAcumulada = this.deudaAcumulada,
        estado = this.estado
    )
}