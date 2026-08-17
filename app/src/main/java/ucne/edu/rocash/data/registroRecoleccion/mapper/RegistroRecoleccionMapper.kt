package ucne.edu.rocash.data.registroRecoleccion.mapper

import ucne.edu.rocash.data.registroRecoleccion.local.RegistroRecoleccionEntity
import ucne.edu.rocash.data.registroRecoleccion.local.ResumenRecoleccionRutaEntity
import ucne.edu.rocash.domain.registroRecoleccion.model.EstadoVisita
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta

fun RegistroRecoleccionEntity.toDomain(): RegistroRecoleccion = RegistroRecoleccion(
    recoleccionId = recoleccionId,
    hojaRutaId = hojaRutaId,
    estacionId = estacionId,
    ventaBruta = ventaBruta,
    comisionCliente = comisionCliente,
    montoEsperado = montoEsperado,
    montoRecolectado = montoRecolectado,
    montoDeuda = montoDeuda,
    estadoVisita = runCatching { EstadoVisita.valueOf(estadoVisita) }
        .getOrDefault(EstadoVisita.COMPLETADA),
    notaIncidencia = notaIncidencia
)

fun RegistroRecoleccion.toEntity(): RegistroRecoleccionEntity = RegistroRecoleccionEntity(
    recoleccionId = recoleccionId,
    hojaRutaId = hojaRutaId,
    estacionId = estacionId,
    ventaBruta = ventaBruta,
    comisionCliente = comisionCliente,
    montoEsperado = montoEsperado,
    montoRecolectado = montoRecolectado,
    montoDeuda = montoDeuda,
    estadoVisita = estadoVisita.name,
    notaIncidencia = notaIncidencia
)

fun ResumenRecoleccionRutaEntity.toDomain(): ResumenRecoleccionRuta = ResumenRecoleccionRuta(
    totalVentaBruta = totalVentaBruta,
    totalComisionClientes = totalComisionClientes,
    totalRecaudado = totalRecaudado,
    totalDeudas = totalDeudas,
    cantidadRegistros = cantidadRegistros
)