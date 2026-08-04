package ucne.edu.rocash.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ucne.edu.rocash.data.local.database.RoCashDatabase
import ucne.edu.rocash.data.repository.RoCashRepositoryImpl
import ucne.edu.rocash.domain.repository.RoCashRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoCashDatabase(@ApplicationContext context: Context): RoCashDatabase {
        return Room.databaseBuilder(
            context,
            RoCashDatabase::class.java,
            RoCashDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRoCashRepository(database: RoCashDatabase): RoCashRepository {
        return RoCashRepositoryImpl(database.roCashDao)
    }
}