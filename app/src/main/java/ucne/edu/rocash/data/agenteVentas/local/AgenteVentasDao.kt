package ucne.edu.rocash.data.agenteVentas.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AgenteVentasDao {
        @Upsert
        suspend fun upsert(entity: AgenteVentasEntity): Long

        @Delete
        suspend fun delete(entity: AgenteVentasEntity)

        @Query("SELECT * FROM agentes_ventas ORDER BY agenteId DESC")
        fun observeAll(): Flow<List<AgenteVentasEntity>>

        @Query("SELECT * FROM agentes_ventas WHERE agenteId = :id")
        suspend fun getById(id: Int): AgenteVentasEntity?

        @Query("DELETE FROM agentes_ventas WHERE agenteId = :id")
        suspend fun deleteById(id: Int)

        @Query("SELECT EXISTS(SELECT 1 FROM agentes_ventas WHERE agenteId = :id)")
        suspend fun exists(id: Int): Boolean

        @Query("SELECT * FROM agentes_ventas WHERE nombre LIKE '%' || :nombre || '%'")
        fun searchByName(nombre: String): Flow<List<AgenteVentasEntity>>

        @Query("UPDATE agentes_ventas SET deudaAcumulada = deudaAcumulada + :monto WHERE agenteId = :id")
        suspend fun sumarDeuda(id: Int, monto: Double)
}