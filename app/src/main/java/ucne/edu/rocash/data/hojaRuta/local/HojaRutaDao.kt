package ucne.edu.rocash.data.hojaRuta.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.data.estacion.local.HojaRutaConEstaciones

@Dao
interface HojaRutaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRuta(ruta: HojaRutaEntity): Long

    @Update
    suspend fun actualizarRuta(ruta: HojaRutaEntity)

    @Transaction
    @Query("SELECT * FROM hoja_ruta WHERE recolectorId = :recolectorId AND estado = 'EN_PROGRESO' LIMIT 1")
    fun obtenerRutaActiva(recolectorId: String): Flow<HojaRutaConEstaciones?>

    @Query("SELECT * FROM hoja_ruta WHERE recolectorId = :recolectorId AND estado = 'CERRADA' ORDER BY fechaCreacion DESC")
    fun obtenerHistorial(recolectorId: String): Flow<List<HojaRutaEntity>>

    @Query("SELECT SUM(totalRecaudado) FROM hoja_ruta WHERE recolectorId = :recolectorId AND estado = 'CERRADA'")
    fun obtenerTotalIngresos(recolectorId: String): Flow<Double?> // Puede ser null si no hay rutas

    @Query("SELECT COUNT(*) FROM hoja_ruta WHERE recolectorId = :recolectorId AND estado = 'CERRADA'")
    fun obtenerTotalRutasCompletadas(recolectorId: String): Flow<Int>
}