package ucne.edu.rocash.domain.auth.usecase

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import ucne.edu.rocash.domain.auth.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(context: Context): Result<FirebaseUser> = repository.signInWithGoogle(context)
}