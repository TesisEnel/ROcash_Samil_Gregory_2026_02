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

    @Query("SELECT COALESCE(SUM(monto), 0) FROM abono_deuda WHERE agenteId = :agenteId")
    fun observarTotalAbonado(agenteId: Int): Flow<Double>
}
