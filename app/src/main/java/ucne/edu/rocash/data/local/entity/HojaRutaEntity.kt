package ucne.edu.rocash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hoja_ruta")
data class HojaRutaEntity(
    @PrimaryKey val id: String,
    val recolectorId: String,
    val fechaCreacion: Long,
    val estado: String
)