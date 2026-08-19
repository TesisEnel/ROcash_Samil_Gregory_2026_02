package ucne.edu.rocash.data.registroRecoleccion.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaEntity

@Entity(
    tableName = "registro_recoleccion",
    foreignKeys = [
        ForeignKey(
            entity = HojaRutaEntity::class,
            parentColumns = ["id"],
            childColumns = ["hojaRutaId"],
            onDelete = ForeignKey.Companion.RESTRICT
        ),
        ForeignKey(
            entity = EstacionVentasEntity::class,
            parentColumns = ["estacionId"],
            childColumns = ["estacionId"],
            onDelete = ForeignKey.Companion.RESTRICT
        )
    ]
)
data class RegistroRecoleccionEntity(
    @PrimaryKey(autoGenerate = true)
    val recoleccionId: Int = 0,

    @ColumnInfo(index = true) val hojaRutaId: Int,
    @ColumnInfo(index = true) val estacionId: Int,

    val ventaBruta: Double,
    val comisionCliente: Double,
    val montoEsperado: Double,
    val montoRecolectado: Double,
    val montoDeuda: Double,
    val estadoVisita: String,
    val notaIncidencia: String?
)
