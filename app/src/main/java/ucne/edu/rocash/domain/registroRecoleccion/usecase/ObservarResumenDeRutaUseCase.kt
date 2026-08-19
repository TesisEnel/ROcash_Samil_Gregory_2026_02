package ucne.edu.rocash.domain.registroRecoleccion.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository
import javax.inject.Inject

class ObservarResumenDeRutaUseCase @Inject constructor(
    private val repository: RegistroRecoleccionRepository
) {
    operator fun invoke(rutaId: Int): Flow<ResumenRecoleccionRuta> =
        repository.observarResumenDeRuta(rutaId)
}
