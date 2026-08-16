package ucne.edu.rocash.domain.registroRecoleccion.usecase

import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository
import javax.inject.Inject

class ObtenerCuadreDeEstacionUseCase @Inject constructor(
    private val repository: RegistroRecoleccionRepository
) {
    suspend operator fun invoke(rutaId: Int, estacionId: Int): RegistroRecoleccion? =
        repository.obtenerPorRutaYEstacion(rutaId, estacionId)
}