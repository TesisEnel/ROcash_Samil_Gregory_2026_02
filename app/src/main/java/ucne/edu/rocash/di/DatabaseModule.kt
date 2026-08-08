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
import ucne.edu.rocash.data.local.dao.RoCashDao
import ucne.edu.rocash.data.local.database.RoCashDatabase
import ucne.edu.rocash.data.recolector.local.RecolectorDao
import ucne.edu.rocash.data.recolector.repository.RecolectorRepositoryImpl
import ucne.edu.rocash.data.repository.RoCashRepositoryImpl
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import ucne.edu.rocash.domain.recolector.repository.RecolectorRepository
import ucne.edu.rocash.domain.repository.RoCashRepository
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
    fun provideRoCashDao(database: RoCashDatabase): RoCashDao {
        return database.roCashDao
    }

    @Provides
    @Singleton
    fun provideRoCashRepository(
        dao: RoCashDao
    ): RoCashRepository {
        return RoCashRepositoryImpl(dao)
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
}