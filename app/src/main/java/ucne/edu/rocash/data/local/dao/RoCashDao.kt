package ucne.edu.rocash.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.data.local.entity.EstacionVentasEntity
import ucne.edu.rocash.data.local.entity.HojaRutaEntity
import ucne.edu.rocash.data.local.entity.RegistroRecoleccionEntity
import ucne.edu.rocash.domain.model.EstacionVentas
import ucne.edu.rocash.domain.model.HojaRuta

@Dao
interface RoCashDao {
    @Query("SELECT * FROM hoja_ruta WHERE recolectorId = :recolectorId AND (estado = 'PENDIENTE' OR estado = 'EN_PROGRESO') LIMIT 1")
    fun obtenerHojaRutaActiva(recolectorId: String): Flow<HojaRutaEntity?>

    @Query("SELECT * FROM estacion_ventas WHERE hojaRutaId = :rutaId")
    fun obtenerTodasLasEstaciones(rutaId: String): Flow<List<EstacionVentasEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRegistroRecoleccion(registro: RegistroRecoleccionEntity): Long

    @Query("UPDATE agente_ventas SET deudaAcumulada = deudaAcumulada + :nuevaDeuda WHERE id = :agenteId")
    suspend fun sumarDeudaAgente(agenteId: String, nuevaDeuda: Double)

    @Query("UPDATE hoja_ruta SET estado = 'CERRADA' WHERE id = :hojaRutaId")
    suspend fun cerrarHojaRuta(hojaRutaId: String)

    @Upsert
    suspend fun insertarHojaRuta(hojaRuta: HojaRutaEntity)

    @Upsert
    suspend fun insertarEstacion(estacion: EstacionVentasEntity)
}