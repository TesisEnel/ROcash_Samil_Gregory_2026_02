package ucne.edu.rocash.domain.auth.usecase

import com.google.firebase.auth.FirebaseUser
import ucne.edu.rocash.domain.auth.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUser> {
        val emailResult = validateEmail(email)
        if (!emailResult.isValid) return Result.failure(IllegalArgumentException(emailResult.error))

        val passwordResult = validatePassword(password)
        if (!passwordResult.isValid) return Result.failure(IllegalArgumentException(passwordResult.error))

        return repository.signInWithEmail(email, password)
    }
}