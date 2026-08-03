package ucne.edu.rocash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hoja_ruta")
data class HojaRutaEntity(
    @PrimaryKey(autoGenerate = true) val id: String = "0",
    val recolectorId: Int,
    val fechaCreacion: Long,
    val estado: String
)