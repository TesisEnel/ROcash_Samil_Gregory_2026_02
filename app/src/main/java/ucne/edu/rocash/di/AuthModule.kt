package ucne.edu.rocash.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ucne.edu.rocash.data.auth.repository.AuthRepositoryImpl
import ucne.edu.rocash.data.auth.session.SesionRecolectorImpl
import ucne.edu.rocash.domain.auth.repository.AuthRepository
import ucne.edu.rocash.domain.auth.session.SesionRecolector
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        credentialManager: CredentialManager
    ): AuthRepository = AuthRepositoryImpl(auth, credentialManager)

    @Provides
    @Singleton
    fun provideSesionRecolector(auth: FirebaseAuth): SesionRecolector =
        SesionRecolectorImpl(auth)
}