package ucne.edu.rocash.data.hojaRuta.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hoja_ruta")
data class HojaRutaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(index = true) val recolectorId: String,

    val fechaCreacion: Long,
    val fechaCierre: Long? = null,

    @ColumnInfo(index = true) val estado: String,

    val totalVentaBruta: Double = 0.0,
    val totalComisionClientes: Double = 0.0,
    val totalRecaudado: Double = 0.0,
    val totalDeudas: Double = 0.0
)
