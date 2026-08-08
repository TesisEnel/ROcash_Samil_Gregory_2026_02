package ucne.edu.rocash.data.recolector.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recolectores")
data class RecolectorEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val telefono: String,
    val estado: Boolean
)