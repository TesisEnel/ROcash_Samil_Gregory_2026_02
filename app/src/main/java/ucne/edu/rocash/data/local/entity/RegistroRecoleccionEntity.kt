package ucne.edu.rocash.data.local.entity

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
            childColumns = ["hojaRutaId"]
        ),
        ForeignKey(entity = EstacionVentasEntity::class, parentColumns = ["id"], childColumns = ["estacionId"])
    ]
)
data class RegistroRecoleccionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val hojaRutaId: String,
    @ColumnInfo(index = true) val estacionId: String,
    val ventaBruta: Double,
    val porcentajeCliente: Double,
    val montoEsperado: Double,
    val montoRecolectado: Double,
    val montoDeuda: Double,
    val estadoVisita: String,
    val notaIncidencia: String?
)