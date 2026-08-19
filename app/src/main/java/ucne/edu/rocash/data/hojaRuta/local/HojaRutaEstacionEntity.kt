package ucne.edu.rocash.data.hojaRuta.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity

@Entity(
    tableName = "hoja_ruta_estacion",
    primaryKeys = ["hojaRutaId", "estacionId"],
    foreignKeys = [
        ForeignKey(
            entity = HojaRutaEntity::class,
            parentColumns = ["id"],
            childColumns = ["hojaRutaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EstacionVentasEntity::class,
            parentColumns = ["estacionId"],
            childColumns = ["estacionId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class HojaRutaEstacionEntity(
    @ColumnInfo(index = true) val hojaRutaId: Int,
    @ColumnInfo(index = true) val estacionId: Int,
    val orden: Int = 0,
    val estadoVisita: String
)
