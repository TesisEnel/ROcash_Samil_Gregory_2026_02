package ucne.edu.rocash.data.hojaRuta.mapper

import ucne.edu.rocash.data.estacion.local.HojaRutaConEstaciones
import ucne.edu.rocash.data.estacion.mapper.toDomain
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaEntity
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

fun HojaRuta.toEntity(): HojaRutaEntity {
    return HojaRutaEntity(
        id = this.id,
        recolectorId = this.recolectorId,
        fechaCreacion = this.fechaCreacion,
        estado = this.estado.name,
        totalVentaBruta = this.totalVentaBruta,
        totalComisionClientes = this.totalComisionClientes,
        totalRecaudado = this.totalRecaudado,
        totalDeudas = this.totalDeudas
    )
}

fun HojaRutaEntity.toDomain(): HojaRuta {
    return HojaRuta(
        id = this.id,
        recolectorId = this.recolectorId,
        fechaCreacion = this.fechaCreacion,
        estado = EstadoRuta.valueOf(this.estado),
        totalVentaBruta = this.totalVentaBruta,
        totalComisionClientes = this.totalComisionClientes,
        totalRecaudado = this.totalRecaudado,
        totalDeudas = this.totalDeudas,
        estaciones = emptyList()
    )
}

fun HojaRutaConEstaciones.toDomain(): HojaRuta {
    return this.ruta.toDomain().copy(
        estaciones = this.estaciones.map { it.toDomain() }
    )
}