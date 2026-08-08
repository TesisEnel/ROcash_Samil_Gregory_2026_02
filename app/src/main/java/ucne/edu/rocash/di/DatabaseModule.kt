package ucne.edu.rocash.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ucne.edu.rocash.data.local.dao.RoCashDao
import ucne.edu.rocash.data.local.database.RoCashDatabase
import ucne.edu.rocash.data.recolector.local.RecolectorDao
import ucne.edu.rocash.data.repository.RoCashRepositoryImpl
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
        return RoCashRepositoryImpl(dao, recolectorDao)
    }

    @Provides
    @Singleton
    fun provideRecolectorRepository(
        recolectorDao: RecolectorDao
    ): RecolectorRepository {
        return RecolectorRepositoryImpl(recolectorDao)
    }
}