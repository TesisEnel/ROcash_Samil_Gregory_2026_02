package ucne.edu.rocash.domain.agenteVentas.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

interface AgenteVentasRepository {
    fun observeAgentes(): Flow<List<AgenteVentas>>
    suspend fun getAgente(id: Int): AgenteVentas?
    suspend fun upsert(agente: AgenteVentas): Int
    suspend fun delete(id: Int)
    suspend fun exists(id: Int): Boolean
    fun buscarAgentesPorNombre(nombre: String): Flow<List<AgenteVentas>>
}