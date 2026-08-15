package ucne.edu.rocash.domain.auth.usecase

import com.google.firebase.auth.FirebaseUser
import ucne.edu.rocash.domain.auth.repository.AuthRepository
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): FirebaseUser? = repository.getCurrentUser()
}