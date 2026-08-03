package ucne.edu.rocash.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "estacion_ventas",
    foreignKeys = [
        ForeignKey(
            entity = AgenteVentasEntity::class,
            parentColumns = ["id"],
            childColumns = ["agenteId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class EstacionVentasEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val direccion: String,
    @ColumnInfo(index = true) val agenteId: Int
)