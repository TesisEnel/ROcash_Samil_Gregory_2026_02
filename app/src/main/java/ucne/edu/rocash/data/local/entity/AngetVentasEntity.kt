package ucne.edu.rocash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "agente_ventas")
data class AgenteVentasEntity(
    @PrimaryKey(autoGenerate = true) val id: String = "0",
    val nombre: String,
    val telefono: String,
    val deudaAcumulada: Double = 0.0
)