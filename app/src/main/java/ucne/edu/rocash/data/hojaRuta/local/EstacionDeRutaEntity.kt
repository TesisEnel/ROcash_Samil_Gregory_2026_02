package ucne.edu.rocash.data.hojaRuta.local

import androidx.room.Embedded
import androidx.room.Relation
import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity

data class EstacionDeRutaEntity(
    @Embedded val cruce: HojaRutaEstacionEntity,

    @Relation(
        parentColumn = "estacionId",
        entityColumn = "estacionId"
    )
    val estacion: EstacionVentasEntity
)
