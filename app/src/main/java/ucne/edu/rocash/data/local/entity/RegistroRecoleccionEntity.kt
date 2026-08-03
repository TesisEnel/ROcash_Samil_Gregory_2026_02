package ucne.edu.rocash.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "registro_recoleccion",
    foreignKeys = [
        ForeignKey(
            entity = HojaRutaEntity::class,
            parentColumns = ["id"],
            childColumns = ["hojaRutaId"]
        ),
        ForeignKey(entity = EstacionVentasEntity::class, parentColumns = ["id"], childColumns = ["estacionId"])
    ]
)
data class RegistroRecoleccionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(index = true) val hojaRutaId: Int,
    @ColumnInfo(index = true) val estacionId: Int,
    val ventaBruta: Double,
    val porcentajeCliente: Double,
    val montoEsperado: Double,
    val montoRecolectado: Double,
    val montoDeuda: Double,
    val estadoVisita: String,
    val notaIncidencia: String?
)