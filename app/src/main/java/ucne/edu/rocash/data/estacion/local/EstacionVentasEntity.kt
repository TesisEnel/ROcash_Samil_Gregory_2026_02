package ucne.edu.rocash.data.estacion.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasEntity

@Entity(
    tableName = "estacion_ventas",
    foreignKeys = [
        ForeignKey(
            entity = AgenteVentasEntity::class,
            parentColumns = ["id"],
            childColumns = ["agenteId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AgenteVentasEntity::class,
            parentColumns = ["id"],
            childColumns = ["agenteId2"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class EstacionVentasEntity(
    @PrimaryKey val id: String,
    val hojaRutaId: Int? = null,
    val nombre: String,
    val direccion: String,
    @ColumnInfo(index = true) val agenteId: String,
    @ColumnInfo(index = true) val agenteId2: String? = null
)