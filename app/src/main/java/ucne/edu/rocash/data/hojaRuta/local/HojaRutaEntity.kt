package ucne.edu.rocash.data.hojaRuta.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hoja_ruta")
data class HojaRutaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recolectorId: String,
    val fechaCreacion: Long,
    val estado: String,
    val totalVentaBruta: Double,
    val totalPorcentajeClientes: Double,
    val totalRecaudado: Double,
    val totalDeudas: Double
)