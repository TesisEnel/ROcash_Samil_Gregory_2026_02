package ucne.edu.rocash.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ucne.edu.rocash.data.abonoDeuda.local.AbonoDeudaDao
import ucne.edu.rocash.data.abonoDeuda.local.AbonoDeudaEntity
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasDao
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasEntity
import ucne.edu.rocash.data.estacion.local.EstacionVentasDao
import ucne.edu.rocash.data.estacion.local.EstacionVentasEntity
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaDao
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaEntity
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaEstacionEntity
import ucne.edu.rocash.data.recolector.local.RecolectorDao
import ucne.edu.rocash.data.recolector.local.RecolectorEntity
import ucne.edu.rocash.data.registroRecoleccion.local.RegistroRecoleccionDao
import ucne.edu.rocash.data.registroRecoleccion.local.RegistroRecoleccionEntity

@Database(
    entities = [
        AgenteVentasEntity::class,
        EstacionVentasEntity::class,
        HojaRutaEntity::class,
        HojaRutaEstacionEntity::class,
        RegistroRecoleccionEntity::class,
        RecolectorEntity::class,
        AbonoDeudaEntity::class
    ],
    version = 13,
    exportSchema = false
)
abstract class RoCashDatabase : RoomDatabase() {
    abstract fun recolectorDao(): RecolectorDao

    abstract fun agenteVentasDao(): AgenteVentasDao

    abstract fun estacionVentasDao(): EstacionVentasDao

    abstract fun hojaRutaDao(): HojaRutaDao

    abstract fun registroRecoleccionDao(): RegistroRecoleccionDao

    abstract fun abonoDeudaDao(): AbonoDeudaDao

    companion object {
        const val DATABASE_NAME = "rocash_db"
    }
}
