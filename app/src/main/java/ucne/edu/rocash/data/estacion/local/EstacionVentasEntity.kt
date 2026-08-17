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
            parentColumns = ["agenteId"],
            childColumns = ["agenteId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AgenteVentasEntity::class,
            parentColumns = ["agenteId"],
            childColumns = ["agenteId2"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class EstacionVentasEntity(
    @PrimaryKey(autoGenerate = true)
    val estacionId: Int = 0,
    val nombre: String,
    val direccion: String,
    @ColumnInfo(index = true) val agenteId: Int,
    @ColumnInfo(index = true) val agenteId2: Int? = null
)