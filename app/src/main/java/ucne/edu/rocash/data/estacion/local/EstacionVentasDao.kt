package ucne.edu.rocash.data.estacion.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EstacionVentasDao {
    @Upsert
    suspend fun upsert(estacion: EstacionVentasEntity)

    @Query("SELECT * FROM estacion_ventas")
    fun observeAll(): Flow<List<EstacionVentasEntity>>

    @Query("SELECT * FROM estacion_ventas WHERE id = :id")
    suspend fun getById(id: String): EstacionVentasEntity?

    @Query("SELECT * FROM estacion_ventas WHERE nombre LIKE '%' || :query || '%' OR direccion LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<EstacionVentasEntity>>
}