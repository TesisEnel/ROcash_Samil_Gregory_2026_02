package ucne.edu.rocash.data.estacion.local

import androidx.room.Embedded
import androidx.room.Relation
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaEntity

data class HojaRutaConEstaciones(
    @Embedded val ruta: HojaRutaEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "hojaRutaId"
    )
    val estaciones: List<EstacionVentasEntity>
)