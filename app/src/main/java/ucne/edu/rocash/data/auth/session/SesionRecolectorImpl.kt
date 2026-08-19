package ucne.edu.rocash.data.auth.session

import com.google.firebase.auth.FirebaseAuth
import ucne.edu.rocash.domain.auth.session.SesionRecolector
import javax.inject.Inject

class SesionRecolectorImpl @Inject constructor(
    private val auth: FirebaseAuth
) : SesionRecolector {
    override fun recolectorIdOrNull(): String? = auth.currentUser?.uid
}
