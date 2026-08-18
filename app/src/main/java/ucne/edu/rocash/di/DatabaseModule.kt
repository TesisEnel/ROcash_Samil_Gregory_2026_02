package ucne.edu.rocash.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ucne.edu.rocash.data.abonoDeuda.local.AbonoDeudaDao
import ucne.edu.rocash.data.abonoDeuda.repository.AbonoDeudaRepositoryImpl
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasDao
import ucne.edu.rocash.data.agenteVentas.repository.AgenteVentasRepositoryImpl
import ucne.edu.rocash.data.estacion.local.EstacionVentasDao
import ucne.edu.rocash.data.estacion.repository.EstacionRepositoryImpl
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaDao
import ucne.edu.rocash.data.hojaRuta.repository.HojaRutaRepositoryImpl
import ucne.edu.rocash.data.local.database.RoCashDatabase
import ucne.edu.rocash.data.recolector.local.RecolectorDao
import ucne.edu.rocash.data.recolector.repository.RecolectorRepositoryImpl
import ucne.edu.rocash.data.registroRecoleccion.local.RegistroRecoleccionDao
import ucne.edu.rocash.data.registroRecoleccion.repository.RegistroRecoleccionRepositoryImpl
import ucne.edu.rocash.domain.abonoDeuda.repository.AbonoDeudaRepository
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import ucne.edu.rocash.domain.estacion.repository.EstacionRepository
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import ucne.edu.rocash.domain.recolector.repository.RecolectorRepository
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoCashDatabase(@ApplicationContext context: Context): RoCashDatabase =
        Room.databaseBuilder(
            context,
            RoCashDatabase::class.java,
            RoCashDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()


    @Provides
    @Singleton
    fun provideRecolectorDao(db: RoCashDatabase): RecolectorDao = db.recolectorDao()

    @Provides
    @Singleton
    fun provideRecolectorRepository(dao: RecolectorDao): RecolectorRepository =
        RecolectorRepositoryImpl(dao)


    @Provides
    @Singleton
    fun provideAgenteVentasDao(db: RoCashDatabase): AgenteVentasDao = db.agenteVentasDao()

    @Provides
    @Singleton
    fun provideAgenteVentasRepository(dao: AgenteVentasDao): AgenteVentasRepository =
        AgenteVentasRepositoryImpl(dao)


    @Provides
    @Singleton
    fun provideAbonoDeudaDao(db: RoCashDatabase): AbonoDeudaDao = db.abonoDeudaDao()

    @Provides
    @Singleton
    fun provideAbonoDeudaRepository(dao: AbonoDeudaDao): AbonoDeudaRepository =
        AbonoDeudaRepositoryImpl(dao)


    @Provides
    @Singleton
    fun provideEstacionVentasDao(db: RoCashDatabase): EstacionVentasDao = db.estacionVentasDao()

    @Provides
    @Singleton
    fun provideEstacionRepository(dao: EstacionVentasDao): EstacionRepository =
        EstacionRepositoryImpl(dao)


    @Provides
    @Singleton
    fun provideHojaRutaDao(db: RoCashDatabase): HojaRutaDao = db.hojaRutaDao()

    @Provides
    @Singleton
    fun provideHojaRutaRepository(dao: HojaRutaDao): HojaRutaRepository =
        HojaRutaRepositoryImpl(dao)


    @Provides
    @Singleton
    fun provideRegistroRecoleccionDao(db: RoCashDatabase): RegistroRecoleccionDao =
        db.registroRecoleccionDao()

    @Provides
    @Singleton
    fun provideRegistroRecoleccionRepository(
        dao: RegistroRecoleccionDao
    ): RegistroRecoleccionRepository = RegistroRecoleccionRepositoryImpl(dao)
}