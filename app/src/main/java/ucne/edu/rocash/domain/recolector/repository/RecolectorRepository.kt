package ucne.edu.rocash.domain.recolector.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.recolector.model.Recolector

interface RecolectorRepository {
    suspend fun insertarRecolector(recolector: Recolector)
    fun obtenerRecolectores(): Flow<List<Recolector>>
    suspend fun obtenerRecolectorPorId(id: String): Recolector?
    fun buscarRecolectoresPorNombre(nombre: String): Flow<List<Recolector>>
}
