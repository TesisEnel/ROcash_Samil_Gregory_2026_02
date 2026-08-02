package ucne.edu.rocash.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "agente_ventas")
data class AgenteVentasEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val deudaAcumulada: Double = 0.0
)