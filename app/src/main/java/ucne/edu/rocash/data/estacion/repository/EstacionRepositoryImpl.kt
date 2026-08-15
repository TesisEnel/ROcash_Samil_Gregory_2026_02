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
    private val localDataSource: EstacionVentasDao
) : EstacionRepository {

    override fun observeEstaciones(): Flow<List<EstacionVentas>> =
        localDataSource.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getEstacion(id: Int): EstacionVentas? =
        localDataSource.getById(id)?.toDomain()

    override suspend fun upsert(estacion: EstacionVentas): Int {
        localDataSource.upsert(estacion.toEntity())
        return estacion.estacionId
    }

    override suspend fun delete(id: Int) = localDataSource.deleteById(id)

    override suspend fun exists(id: Int): Boolean = localDataSource.exists(id)

    override fun buscarEstaciones(query: String): Flow<List<EstacionVentas>> =
        localDataSource.search(query).map { lista -> lista.map { it.toDomain() } }
}