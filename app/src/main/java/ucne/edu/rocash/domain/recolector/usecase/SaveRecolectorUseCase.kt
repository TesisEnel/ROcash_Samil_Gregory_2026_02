package ucne.edu.rocash.domain.recolector.usecase

import ucne.edu.rocash.domain.recolector.model.Recolector
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

class SaveRecolectorUseCase @Inject constructor(
    private val repository: RoCashRepository
) {
    suspend operator fun invoke(recolector: Recolector) {
        if (recolector.nombre.isBlank()) {
            throw IllegalArgumentException("El nombre del recolector no puede estar vacío")
        }
        repository.insertarRecolector(recolector)
    }
}