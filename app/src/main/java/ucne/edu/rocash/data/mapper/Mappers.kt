package ucne.edu.rocash.data.mapper

import ucne.edu.rocash.data.local.entity.EstacionVentasEntity
import ucne.edu.rocash.data.local.entity.HojaRutaEntity
import ucne.edu.rocash.data.local.entity.RegistroRecoleccionEntity
import ucne.edu.rocash.domain.model.EstacionVentas
import ucne.edu.rocash.domain.model.EstadoRuta
import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.model.RegistroRecoleccion

fun HojaRutaEntity.toDomain(): HojaRuta {
    return HojaRuta(
        id = this.id,
        recolectorId = this.recolectorId,
        fechaCreacion = this.fechaCreacion,
        estado = EstadoRuta.valueOf(this.estado)
    )
}

fun EstacionVentasEntity.toDomain(): EstacionVentas {
    return EstacionVentas(
        id = this.id,
        hojaRutaId = hojaRutaId,
        nombre = this.nombre,
        direccion = this.direccion,
        agenteId = this.agenteId
    )
}

fun RegistroRecoleccion.toEntity(): RegistroRecoleccionEntity {
    return RegistroRecoleccionEntity(
        id = this.id,
        hojaRutaId = this.hojaRutaId,
        estacionId = this.estacionId,
        ventaBruta = this.ventaBruta,
        porcentajeCliente = this.porcentajeCliente,
        montoEsperado = this.montoEsperado,
        montoRecolectado = this.montoRecolectado,
        montoDeuda = this.montoDeuda,
        estadoVisita = this.estadoVisita.name,
        notaIncidencia = this.notaIncidencia
    )
}

fun HojaRuta.toEntity(): HojaRutaEntity {
    return HojaRutaEntity(
        id = this.id,
        recolectorId = this.recolectorId,
        fechaCreacion = this.fechaCreacion,
        estado = this.estado.name
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