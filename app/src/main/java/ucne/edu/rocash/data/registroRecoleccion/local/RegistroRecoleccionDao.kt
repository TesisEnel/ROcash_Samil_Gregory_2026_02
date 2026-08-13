package ucne.edu.rocash.data.registroRecoleccion.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroRecoleccionDao {
    @Upsert
    suspend fun upsert(entity: RegistroRecoleccionEntity)

    @Delete
    suspend fun delete(entity: RegistroRecoleccionEntity)

    @Query("SELECT * FROM registro_recoleccion ORDER BY recoleccionId DESC")
    fun observeAll(): Flow<List<RegistroRecoleccionEntity>>

    @Query("SELECT * FROM registro_recoleccion WHERE recoleccionId = :id")
    suspend fun getById(id: Int): RegistroRecoleccionEntity?

    @Query("DELETE FROM registro_recoleccion WHERE recoleccionId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM registro_recoleccion WHERE recoleccionId = :id)")
    suspend fun exists(id: Int): Boolean

    @Query("SELECT * FROM registro_recoleccion WHERE hojaRutaId = :rutaId")
    fun observePorRuta(rutaId: Int): Flow<List<RegistroRecoleccionEntity>>
}