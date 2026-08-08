package ucne.edu.rocash.data.agenteVentas.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.agenteVentas.local.AgenteVentasDao
import ucne.edu.rocash.data.agenteVentas.mapper.toDomain
import ucne.edu.rocash.data.agenteVentas.mapper.toEntity
import ucne.edu.rocash.domain.agenteVentas.model.AgenteVentas
import ucne.edu.rocash.domain.agenteVentas.repository.AgenteVentasRepository
import javax.inject.Inject

class AgenteVentasRepositoryImpl @Inject constructor(
    private val dao: AgenteVentasDao
) : AgenteVentasRepository {

    override suspend fun insertarAgente(agente: AgenteVentas) {
        dao.upsert(agente.toEntity())
    }

    override fun obtenerAgentes(): Flow<List<AgenteVentas>> {
        return dao.observeAll().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun obtenerAgentePorId(id: String): AgenteVentas? {
        return dao.getById(id)?.toDomain()
    }

    override fun buscarAgentesPorNombre(nombre: String): Flow<List<AgenteVentas>> {
        return dao.searchByName(nombre).map { lista ->
            lista.map { it.toDomain() }
        }
    }
}