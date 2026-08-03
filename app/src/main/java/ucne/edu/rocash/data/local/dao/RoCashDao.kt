package ucne.edu.rocash.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.data.local.entity.EstacionVentasEntity
import ucne.edu.rocash.data.local.entity.HojaRutaEntity
import ucne.edu.rocash.data.local.entity.RegistroRecoleccionEntity

@Dao
interface RoCashDao {
    @Query("SELECT * FROM hoja_ruta WHERE recolectorId = :recolectorId AND (estado = 'PENDIENTE' OR estado = 'EN_PROGRESO') LIMIT 1")
    fun obtenerHojaRutaActiva(recolectorId: Int): Flow<HojaRutaEntity?>

    @Query("SELECT * FROM estacion_ventas")
    fun obtenerTodasLasEstaciones(): Flow<List<EstacionVentasEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRegistroRecoleccion(registro: RegistroRecoleccionEntity): Long

    @Query("UPDATE agente_ventas SET deudaAcumulada = deudaAcumulada + :nuevaDeuda WHERE id = :agenteId")
    suspend fun sumarDeudaAgente(agenteId: Int, nuevaDeuda: Double)

    @Query("UPDATE hoja_ruta SET estado = 'CERRADA' WHERE id = :hojaRutaId")
    suspend fun cerrarHojaRuta(hojaRutaId: Int)
}