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
    private val localDataSource: AgenteVentasDao
) : AgenteVentasRepository {
    override fun observeAgentes(): Flow<List<AgenteVentas>> =
        localDataSource.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAgente(id: Int): AgenteVentas? =
        localDataSource.getById(id)?.toDomain()

    override suspend fun upsert(agente: AgenteVentas): Int {
        val rowId = localDataSource.upsert(agente.toEntity())
        return if (rowId > 0) rowId.toInt() else agente.agenteId
    }

    override suspend fun delete(id: Int) = localDataSource.deleteById(id)

    override suspend fun exists(id: Int): Boolean = localDataSource.exists(id)

    override fun buscarAgentesPorNombre(nombre: String): Flow<List<AgenteVentas>> =
        localDataSource.searchByName(nombre).map { lista -> lista.map { it.toDomain() } }

    override suspend fun sumarDeuda(agenteId: Int, monto: Double) {
        localDataSource.sumarDeuda(agenteId, monto)
    }
}
