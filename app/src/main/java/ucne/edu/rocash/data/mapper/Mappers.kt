package ucne.edu.rocash.data.mapper

import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity
import ucne.edu.rocash.data.local.entity.RegistroRecoleccionEntity
import ucne.edu.rocash.data.recolector.local.RecolectorEntity
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.model.RegistroRecoleccion
import ucne.edu.rocash.domain.recolector.model.Recolector

fun RegistroRecoleccion.toEntity(): RegistroRecoleccionEntity {
    return RegistroRecoleccionEntity(
        id = this.id,
        hojaRutaId = this.hojaRutaId,
        estacionId = this.estacionId,
        ventaBruta = this.ventaBruta,
        comisionCliente = this.comisionCliente,
        montoEsperado = this.montoEsperado,
        montoRecolectado = this.montoRecolectado,
        montoDeuda = this.montoDeuda,
        estadoVisita = this.estadoVisita.name,
        notaIncidencia = this.notaIncidencia
    )
}
