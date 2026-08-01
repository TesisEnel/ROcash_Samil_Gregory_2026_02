package ucne.edu.rocash.domain.auth.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser>
    suspend fun signInWithEmail(email: String, password: String): Result
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
}