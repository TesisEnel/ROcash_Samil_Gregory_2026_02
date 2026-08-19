package ucne.edu.rocash.data.abonoDeuda.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasEntity

@Entity(
    tableName = "abono_deuda",
    foreignKeys = [
        ForeignKey(
            entity = AgenteVentasEntity::class,
            parentColumns = ["agenteId"],
            childColumns = ["agenteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AbonoDeudaEntity(
    @PrimaryKey(autoGenerate = true)
    val abonoId: Int = 0,

    @ColumnInfo(index = true) val agenteId: Int,

    val monto: Double,
    val deudaAntes: Double,
    val deudaDespues: Double,
    val fecha: Long,
    val nota: String? = null
)
