package ucne.edu.rocash.data.local.entity

import androidx.room.Entity

@Entity(tableName = "hoja_ruta")
data class HojaRutaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recolectorId: Int,
    val fechaCreacion: Long,
    val estado: String
)