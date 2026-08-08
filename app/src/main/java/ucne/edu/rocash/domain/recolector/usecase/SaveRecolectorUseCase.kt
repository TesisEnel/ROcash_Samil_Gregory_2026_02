package ucne.edu.rocash.domain.recolector.usecase

import ucne.edu.rocash.domain.recolector.model.Recolector
import ucne.edu.rocash.domain.recolector.repository.RecolectorRepository
import javax.inject.Inject

class SaveRecolectorUseCase @Inject constructor(
    private val repository: RecolectorRepository
) {
    suspend operator fun invoke(recolector: Recolector) {
        if (recolector.nombre.isBlank()) {
            throw IllegalArgumentException("El nombre del recolector no puede estar vacío")
        }
        repository.insertarRecolector(recolector)
    }
}