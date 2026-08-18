package ucne.edu.rocash.data.abonoDeuda.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AbonoDeudaDao {

    @Insert
    suspend fun insertar(entity: AbonoDeudaEntity): Long

    @Query("SELECT * FROM abono_deuda WHERE agenteId = :agenteId ORDER BY fecha DESC")
    fun observarPorAgente(agenteId: Int): Flow<List<AbonoDeudaEntity>>

    /**
     * El total lo agrega SQLite, no la capa de presentación.
     *
     * COALESCE porque SUM() sobre cero filas devuelve NULL, y un agente sin
     * abonos ha abonado 0.00, no "nada".
     */
    @Query("SELECT COALESCE(SUM(monto), 0) FROM abono_deuda WHERE agenteId = :agenteId")
    fun observarTotalAbonado(agenteId: Int): Flow<Double>
}
