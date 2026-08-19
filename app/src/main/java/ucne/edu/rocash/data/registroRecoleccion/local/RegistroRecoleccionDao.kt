package ucne.edu.rocash.data.registroRecoleccion.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroRecoleccionDao {
    @Upsert
    suspend fun upsert(entity: RegistroRecoleccionEntity): Long

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

    @Query(
        """
        SELECT * FROM registro_recoleccion
        WHERE hojaRutaId = :rutaId AND estacionId = :estacionId
        LIMIT 1
        """
    )
    suspend fun getPorRutaYEstacion(rutaId: Int, estacionId: Int): RegistroRecoleccionEntity?

    @Query(
        """
        SELECT
            COALESCE(SUM(ventaBruta), 0.0)       AS totalVentaBruta,
            COALESCE(SUM(comisionCliente), 0.0)  AS totalComisionClientes,
            COALESCE(SUM(montoRecolectado), 0.0) AS totalRecaudado,
            COALESCE(SUM(montoDeuda), 0.0)       AS totalDeudas,
            COUNT(*)                             AS cantidadRegistros
        FROM registro_recoleccion
        WHERE hojaRutaId = :rutaId
        """
    )
    suspend fun obtenerResumenDeRuta(rutaId: Int): ResumenRecoleccionRutaEntity

    @Query(
        """
        SELECT
            COALESCE(SUM(ventaBruta), 0.0)       AS totalVentaBruta,
            COALESCE(SUM(comisionCliente), 0.0)  AS totalComisionClientes,
            COALESCE(SUM(montoRecolectado), 0.0) AS totalRecaudado,
            COALESCE(SUM(montoDeuda), 0.0)       AS totalDeudas,
            COUNT(*)                             AS cantidadRegistros
        FROM registro_recoleccion
        WHERE hojaRutaId = :rutaId
        """
    )
    fun observarResumenDeRuta(rutaId: Int): Flow<ResumenRecoleccionRutaEntity>
}
