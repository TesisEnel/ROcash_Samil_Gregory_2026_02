package ucne.edu.rocash.data.agenteVentas.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AgenteVentasDao {
    @Upsert
    suspend fun upsert(agente: AgenteVentasEntity)

    @Query("SELECT * FROM agentes_ventas")
    fun observeAll(): Flow<List<AgenteVentasEntity>>

    @Query("SELECT * FROM agentes_ventas WHERE id = :id")
    suspend fun getById(id: String): AgenteVentasEntity?

    @Query("SELECT * FROM agentes_ventas WHERE nombre LIKE '%' || :nombre || '%'")
    fun searchByName(nombre: String): Flow<List<AgenteVentasEntity>>
}