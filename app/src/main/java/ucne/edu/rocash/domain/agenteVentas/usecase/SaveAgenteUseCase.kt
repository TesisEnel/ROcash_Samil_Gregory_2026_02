package ucne.edu.rocash.domain.agenteVentas.usecase

import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject

class SaveAgenteUseCase @Inject constructor(
    private val repository: AgenteVentasRepository
) {
    suspend operator fun invoke(agente: AgenteVentas) {
        if (agente.nombre.isBlank()) throw IllegalArgumentException("El nombre no puede estar vacío")
        repository.insertarAgente(agente)
    }
}