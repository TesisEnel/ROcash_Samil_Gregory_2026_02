package ucne.edu.rocash.data.agenteVentas.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agentes_ventas")
data class AgenteVentasEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val telefono: String,
    val deudaAcumulada: Double,
    val estado: Boolean
)