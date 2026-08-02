package ucne.edu.rocash.data.local.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import ucne.edu.rocash.data.local.dao.RoCashDao
import ucne.edu.rocash.data.local.entity.AgenteVentasEntity
import ucne.edu.rocash.data.local.entity.EstacionVentasEntity
import ucne.edu.rocash.data.local.entity.HojaRutaEntity
import ucne.edu.rocash.data.local.entity.RegistroRecoleccionEntity

@Database(
    entities = [
        AgenteVentasEntity::class,
        EstacionVentasEntity::class,
        HojaRutaEntity::class,
        RegistroRecoleccionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RoCashDatabase : RoomDatabase() {
    abstract val roCashDao: RoCashDao

    companion object {
        const val DATABASE_NAME = "rocash_db"
    }
}