package ucne.edu.rocash.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "hoja_ruta")
data class HojaRutaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recolectorId: Int,
    val fechaCreacion: Long,
    val estado: String
)