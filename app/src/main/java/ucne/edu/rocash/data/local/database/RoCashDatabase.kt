package ucne.edu.rocash.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasDao
import ucne.edu.rocash.data.local.dao.RoCashDao
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasEntity
import ucne.edu.rocash.data.estacion.local.EstacionVentasDao
import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaDao
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaEntity
import ucne.edu.rocash.data.local.entity.RegistroRecoleccionEntity
import ucne.edu.rocash.data.recolector.local.RecolectorDao
import ucne.edu.rocash.data.recolector.local.RecolectorEntity

@Database(
    entities = [
        AgenteVentasEntity::class,
        EstacionVentasEntity::class,
        HojaRutaEntity::class,
        RegistroRecoleccionEntity::class,
        RecolectorEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class RoCashDatabase : RoomDatabase() {
    abstract val roCashDao: RoCashDao

    abstract fun recolectorDao(): RecolectorDao

    abstract fun agenteVentasDao(): AgenteVentasDao

    abstract fun estacionVentasDao(): EstacionVentasDao

    abstract fun hojaRutaDao(): HojaRutaDao

    companion object {
        const val DATABASE_NAME = "rocash_db"
    }
}