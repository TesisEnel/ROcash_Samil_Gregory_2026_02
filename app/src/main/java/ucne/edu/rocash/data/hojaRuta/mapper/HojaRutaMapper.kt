package ucne.edu.rocash.data.hojaRuta.mapper

import ucne.edu.rocash.data.estacion.mapper.toDomain
import ucne.edu.rocash.data.hojaRuta.local.EstacionDeRutaEntity
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaConEstaciones
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaEntity
import ucne.edu.rocash.domain.hojaRuta.model.EstacionEnRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

fun HojaRuta.toEntity(): HojaRutaEntity = HojaRutaEntity(
    id = id,
    recolectorId = recolectorId,
    fechaCreacion = fechaCreacion,
    fechaCierre = fechaCierre,
    estado = estado.name,
    totalVentaBruta = totalVentaBruta,
    totalComisionClientes = totalComisionClientes,
    totalRecaudado = totalRecaudado,
    totalDeudas = totalDeudas
)

fun HojaRutaEntity.toDomain(): HojaRuta = HojaRuta(
    id = id,
    recolectorId = recolectorId,
    fechaCreacion = fechaCreacion,
    fechaCierre = fechaCierre,
    estado = estado.toEstadoRuta(),
    totalVentaBruta = totalVentaBruta,
    totalComisionClientes = totalComisionClientes,
    totalRecaudado = totalRecaudado,
    totalDeudas = totalDeudas,
    estaciones = emptyList()
)

fun HojaRutaConEstaciones.toDomain(): HojaRuta = ruta.toDomain().copy(
    estaciones = estaciones
        .sortedBy { it.cruce.orden }
        .map { it.toDomain() }
)

fun EstacionDeRutaEntity.toDomain(): EstacionEnRuta = EstacionEnRuta(
    estacion = estacion.toDomain(),
    orden = cruce.orden,
    estado = cruce.estadoVisita.toEstadoVisitaEstacion()
)

private fun String.toEstadoRuta(): EstadoRuta =
    runCatching { EstadoRuta.valueOf(this) }.getOrDefault(EstadoRuta.PENDIENTE)

private fun String.toEstadoVisitaEstacion(): EstadoVisitaEstacion =
    runCatching { EstadoVisitaEstacion.valueOf(this) }
        .getOrDefault(EstadoVisitaEstacion.PENDIENTE)
