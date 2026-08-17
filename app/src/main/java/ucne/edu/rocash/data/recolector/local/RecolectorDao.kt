package ucne.edu.rocash.data.recolector.local
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecolectorDao {

    @Upsert
    suspend fun upsert(recolector: RecolectorEntity)

    @Delete
    suspend fun delete(recolector: RecolectorEntity)

    @Query("SELECT * FROM recolectores")
    fun observeAll(): Flow<List<RecolectorEntity>>

    @Query("SELECT * FROM recolectores WHERE id = :id")
    suspend fun getById(id: String): RecolectorEntity?

    @Query("SELECT * FROM recolectores WHERE nombre LIKE '%' || :nombre || '%'")
    fun searchByName(nombre: String): Flow<List<RecolectorEntity>>
}