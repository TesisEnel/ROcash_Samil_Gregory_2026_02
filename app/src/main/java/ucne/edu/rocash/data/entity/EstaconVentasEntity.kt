package ucne.edu.rocash.data.entity
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

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