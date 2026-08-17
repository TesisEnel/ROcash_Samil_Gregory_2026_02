package ucne.edu.rocash.data.hojaRuta.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HojaRutaDao {

    @Insert
    suspend fun insertar(ruta: HojaRutaEntity): Long

    @Update
    suspend fun actualizar(ruta: HojaRutaEntity)

    @Upsert
    suspend fun upsertEstacionesDeRuta(cruces: List<HojaRutaEstacionEntity>)

    @Query("DELETE FROM hoja_ruta_estacion WHERE hojaRutaId = :rutaId")
    suspend fun borrarEstacionesDeRuta(rutaId: Int)

    @Transaction
    suspend fun crearRutaConEstaciones(
        ruta: HojaRutaEntity,
        estacionIds: List<Int>,
        estadoInicialEstacion: String
    ): Int {
        val nuevoId = insertar(ruta).toInt()
        val cruces = estacionIds.mapIndexed { indice, estacionId ->
            HojaRutaEstacionEntity(
                hojaRutaId = nuevoId,
                estacionId = estacionId,
                orden = indice,
                estadoVisita = estadoInicialEstacion
            )
        }
        upsertEstacionesDeRuta(cruces)
        return nuevoId
    }

    @Query(
        """
        UPDATE hoja_ruta_estacion
        SET estadoVisita = :estado
        WHERE hojaRutaId = :rutaId AND estacionId = :estacionId
        """
    )
    suspend fun marcarEstadoEstacion(rutaId: Int, estacionId: Int, estado: String)

    @Query("UPDATE hoja_ruta SET estado = :estado WHERE id = :rutaId")
    suspend fun cambiarEstado(rutaId: Int, estado: String)

    @Query(
        """
        UPDATE hoja_ruta
        SET estado = :estadoCerrada,
            fechaCierre = :fechaCierre,
            totalVentaBruta = :totalVentaBruta,
            totalComisionClientes = :totalComisionClientes,
            totalRecaudado = :totalRecaudado,
            totalDeudas = :totalDeudas
        WHERE id = :rutaId
        """
    )
    suspend fun cerrarConTotales(
        rutaId: Int,
        estadoCerrada: String,
        fechaCierre: Long,
        totalVentaBruta: Double,
        totalComisionClientes: Double,
        totalRecaudado: Double,
        totalDeudas: Double
    )


    @Transaction
    @Query("SELECT * FROM hoja_ruta WHERE id = :rutaId")
    fun observarRutaConEstaciones(rutaId: Int): Flow<HojaRutaConEstaciones?>

    @Transaction
    @Query("SELECT * FROM hoja_ruta WHERE id = :rutaId")
    suspend fun obtenerRutaConEstaciones(rutaId: Int): HojaRutaConEstaciones?

    @Transaction
    @Query(
        """
        SELECT * FROM hoja_ruta
        WHERE recolectorId = :recolectorId AND estado IN (:estadosAbiertos)
        ORDER BY fechaCreacion DESC
        """
    )
    fun observarRutasAbiertas(
        recolectorId: String,
        estadosAbiertos: List<String>
    ): Flow<List<HojaRutaConEstaciones>>

    @Query(
        """
        SELECT * FROM hoja_ruta
        WHERE recolectorId = :recolectorId AND estado = :estadoCerrada
        ORDER BY fechaCierre DESC, fechaCreacion DESC
        """
    )
    fun observarHistorial(
        recolectorId: String,
        estadoCerrada: String
    ): Flow<List<HojaRutaEntity>>

    @Query(
        """
        SELECT COALESCE(SUM(totalRecaudado), 0.0) FROM hoja_ruta
        WHERE recolectorId = :recolectorId AND estado = :estadoCerrada
        """
    )
    fun observarTotalIngresos(recolectorId: String, estadoCerrada: String): Flow<Double>

    @Query(
        """
        SELECT COUNT(*) FROM hoja_ruta
        WHERE recolectorId = :recolectorId AND estado = :estadoCerrada
        """
    )
    fun observarTotalRutasCompletadas(recolectorId: String, estadoCerrada: String): Flow<Int>

    @Query(
        """
        SELECT hre.estacionId
        FROM hoja_ruta_estacion hre
        INNER JOIN hoja_ruta hr ON hr.id = hre.hojaRutaId
        WHERE hr.estado IN (:estadosAbiertos) AND hre.estacionId IN (:estacionIds)
        """
    )
    suspend fun estacionesYaComprometidas(
        estacionIds: List<Int>,
        estadosAbiertos: List<String>
    ): List<Int>

    @Query(
        """
        SELECT hre.estacionId
        FROM hoja_ruta_estacion hre
        INNER JOIN hoja_ruta hr ON hr.id = hre.hojaRutaId
        WHERE hr.estado IN (:estadosAbiertos)
        """
    )
    fun observarEstacionesComprometidas(estadosAbiertos: List<String>): Flow<List<Int>>
}