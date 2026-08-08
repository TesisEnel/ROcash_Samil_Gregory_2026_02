package ucne.edu.rocash.data.estacion.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.estacion.local.EstacionVentasDao
import ucne.edu.rocash.data.estacion.mapper.toDomain
import ucne.edu.rocash.data.estacion.mapper.toEntity
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.estacion.repository.EstacionRepository
import javax.inject.Inject

class EstacionRepositoryImpl @Inject constructor(
    private val dao: EstacionVentasDao
) : EstacionRepository {

    override suspend fun insertarEstacion(estacion: EstacionVentas) {
        dao.upsert(estacion.toEntity())
    }

    override fun obtenerEstaciones(): Flow<List<EstacionVentas>> {
        return dao.observeAll().map { lista -> lista.map { it.toDomain() } }
    }

    override suspend fun obtenerEstacionPorId(id: String): EstacionVentas? {
        return dao.getById(id)?.toDomain()
    }

    override fun buscarEstaciones(query: String): Flow<List<EstacionVentas>> {
        return dao.search(query).map { lista -> lista.map { it.toDomain() } }
    }
}