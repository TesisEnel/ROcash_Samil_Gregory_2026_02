package ucne.edu.rocash.data.recolector.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.recolector.local.RecolectorDao
import ucne.edu.rocash.data.recolector.mapper.toDomain
import ucne.edu.rocash.data.recolector.mapper.toEntity
import ucne.edu.rocash.domain.recolector.model.Recolector
import ucne.edu.rocash.domain.recolector.repository.RecolectorRepository
import javax.inject.Inject

class RecolectorRepositoryImpl @Inject constructor(
    private val recolectorDao: RecolectorDao
) : RecolectorRepository {

    override suspend fun insertarRecolector(recolector: Recolector) {
        recolectorDao.upsert(recolector.toEntity())
    }

    override fun obtenerRecolectores(): Flow<List<Recolector>> {
        return recolectorDao.observeAll().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun obtenerRecolectorPorId(id: String): Recolector? {
        return recolectorDao.getById(id)?.toDomain()
    }

    override fun buscarRecolectoresPorNombre(nombre: String): Flow<List<Recolector>> {
        return recolectorDao.searchByName(nombre).map { lista ->
            lista.map { it.toDomain() }
        }
    }
}