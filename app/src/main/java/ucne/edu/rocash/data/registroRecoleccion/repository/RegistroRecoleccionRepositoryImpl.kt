package ucne.edu.rocash.data.registroRecoleccion.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.registroRecoleccion.local.RegistroRecoleccionDao
import ucne.edu.rocash.data.registroRecoleccion.mapper.toDomain
import ucne.edu.rocash.data.registroRecoleccion.mapper.toEntity
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository
import javax.inject.Inject

class RegistroRecoleccionRepositoryImpl @Inject constructor(
    private val localDataSource: RegistroRecoleccionDao
) : RegistroRecoleccionRepository {

    override fun observeRecolecciones(): Flow<List<RegistroRecoleccion>> {
        return localDataSource.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getRecoleccion(id: Int): RegistroRecoleccion? {
        return localDataSource.getById(id)?.toDomain()
    }

    override suspend fun upsert(registro: RegistroRecoleccion): Int {
        localDataSource.upsert(registro.toEntity())
        return registro.recoleccionId
    }

    override suspend fun delete(id: Int) {
        localDataSource.deleteById(id)
    }

    override suspend fun exists(id: Int): Boolean {
        return localDataSource.exists(id)
    }
}