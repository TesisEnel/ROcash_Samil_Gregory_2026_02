package ucne.edu.rocash.data.estacion.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EstacionVentasDao {
    @Upsert
    suspend fun upsert(entity: EstacionVentasEntity)

    @Delete
    suspend fun delete(entity: EstacionVentasEntity)

    @Query("SELECT * FROM estacion_ventas ORDER BY nombre ASC")
    fun observeAll(): Flow<List<EstacionVentasEntity>>

    @Query("SELECT * FROM estacion_ventas WHERE estacionId = :id")
    suspend fun getById(id: Int): EstacionVentasEntity?

    @Query("DELETE FROM estacion_ventas WHERE estacionId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM estacion_ventas WHERE estacionId = :id)")
    suspend fun exists(id: Int): Boolean

    @Query(
        """
        SELECT * FROM estacion_ventas
        WHERE nombre LIKE '%' || :query || '%' OR direccion LIKE '%' || :query || '%'
        ORDER BY nombre ASC
        """
    )
    fun search(query: String): Flow<List<EstacionVentasEntity>>

    // `asignarRuta` se elimino: la pertenencia a una ruta ahora la maneja
    // HojaRutaDao a traves de la tabla puente hoja_ruta_estacion.
}