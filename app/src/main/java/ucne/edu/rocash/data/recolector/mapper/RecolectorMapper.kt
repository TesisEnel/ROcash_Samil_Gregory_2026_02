package ucne.edu.rocash.data.recolector.mapper

import ucne.edu.rocash.data.recolector.local.RecolectorEntity
import ucne.edu.rocash.domain.recolector.model.Recolector

fun Recolector.toEntity(): RecolectorEntity
{
    return RecolectorEntity(
        id = this.id,
        nombre = this.nombre,
        telefono = this.telefono,
        estado = this.estado
    )
}

fun RecolectorEntity.toDomain(): Recolector {
    return Recolector(
        id = this.id,
        nombre = this.nombre,
        telefono = this.telefono,
        estado = this.estado
    )
}
