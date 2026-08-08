package ucne.edu.rocash.domain.agenteVentas.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas

interface AgenteVentasRepository {
    suspend fun insertarAgente(agente: AgenteVentas)
    fun obtenerAgentes(): Flow<List<AgenteVentas>>
    suspend fun obtenerAgentePorId(id: String): AgenteVentas?
    fun buscarAgentesPorNombre(nombre: String): Flow<List<AgenteVentas>>
}