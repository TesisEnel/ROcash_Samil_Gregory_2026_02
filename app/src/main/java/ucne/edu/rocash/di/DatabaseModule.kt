package ucne.edu.rocash.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasDao
import ucne.edu.rocash.data.agenteVentas.repository.AgenteVentasRepositoryImpl
import ucne.edu.rocash.data.estacion.local.EstacionVentasDao
import ucne.edu.rocash.data.estacion.repository.EstacionRepositoryImpl
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaDao
import ucne.edu.rocash.data.hojaRuta.repository.HojaRutaRepositoryImpl
import ucne.edu.rocash.data.local.dao.RoCashDao
import ucne.edu.rocash.data.local.database.RoCashDatabase
import ucne.edu.rocash.data.recolector.local.RecolectorDao
import ucne.edu.rocash.data.recolector.repository.RecolectorRepositoryImpl
import ucne.edu.rocash.data.registroRecoleccion.local.RegistroRecoleccionDao
import ucne.edu.rocash.data.registroRecoleccion.repository.RegistroRecoleccionRepositoryImpl
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
    fun provideRoCashDatabase(@ApplicationContext context: Context): RoCashDatabase {
        return Room.databaseBuilder(
            context,
            RoCashDatabase::class.java,
            RoCashDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRecolectorDao(database: RoCashDatabase): RecolectorDao {
        return database.recolectorDao()
    }

    @Provides
    @Singleton
    fun provideRecolectorRepository(
        recolectorDao: RecolectorDao
    ): RecolectorRepository {
        return RecolectorRepositoryImpl(recolectorDao)
    }

    @Provides
    @Singleton
    fun provideAgenteVentasDao(database: RoCashDatabase): AgenteVentasDao {
        return database.agenteVentasDao()
    }

    @Provides
    @Singleton
    fun provideAgenteVentasRepository(
        agenteDao: AgenteVentasDao
    ): AgenteVentasRepository {
        return AgenteVentasRepositoryImpl(agenteDao)
    }

    @Provides
    @Singleton
    fun provideEstacionVentasDao(database: RoCashDatabase): EstacionVentasDao {
        return database.estacionVentasDao()
    }

    @Provides
    @Singleton
    fun provideEstacionRepository(
        dao: EstacionVentasDao
    ): EstacionRepository {
        return EstacionRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideHojaRutaDao(database: RoCashDatabase): HojaRutaDao{
        return database.hojaRutaDao()
    }

    @Provides
    @Singleton
    fun providesHojaRutaRepository(
        dao: HojaRutaDao
    ): HojaRutaRepository{
        return HojaRutaRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideRegistroRecoleccionDao(database: RoCashDatabase): RegistroRecoleccionDao {
        return database.registroRecoleccionDao()
    }

    @Provides
    @Singleton
    fun provideRegistroRecoleccionRepository(
        dao: RegistroRecoleccionDao
    ): RegistroRecoleccionRepository {
        return RegistroRecoleccionRepositoryImpl(dao)
    }
}