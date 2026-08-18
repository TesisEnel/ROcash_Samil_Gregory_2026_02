package ucne.edu.rocash.data.registroRecoleccion.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.registroRecoleccion.local.RegistroRecoleccionDao
import ucne.edu.rocash.data.registroRecoleccion.mapper.toDomain
import ucne.edu.rocash.data.registroRecoleccion.mapper.toEntity
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository
import javax.inject.Inject

class RegistroRecoleccionRepositoryImpl @Inject constructor(
    private val localDataSource: RegistroRecoleccionDao
) : RegistroRecoleccionRepository {

    override fun observeRecolecciones(): Flow<List<RegistroRecoleccion>> =
        localDataSource.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getRecoleccion(id: Int): RegistroRecoleccion? =
        localDataSource.getById(id)?.toDomain()

    override suspend fun upsert(registro: RegistroRecoleccion): Int {
        val rowId = localDataSource.upsert(registro.toEntity())
        return if (rowId > 0) rowId.toInt() else registro.recoleccionId
    }

    override suspend fun delete(id: Int) = localDataSource.deleteById(id)

    override suspend fun exists(id: Int): Boolean = localDataSource.exists(id)

    override fun observarPorRuta(rutaId: Int): Flow<List<RegistroRecoleccion>> =
        localDataSource.observePorRuta(rutaId).map { list -> list.map { it.toDomain() } }

    override suspend fun obtenerPorRutaYEstacion(
        rutaId: Int,
        estacionId: Int
    ): RegistroRecoleccion? =
        localDataSource.getPorRutaYEstacion(rutaId, estacionId)?.toDomain()

    override suspend fun obtenerResumenDeRuta(rutaId: Int): ResumenRecoleccionRuta =
        localDataSource.obtenerResumenDeRuta(rutaId).toDomain()

    override fun observarResumenDeRuta(rutaId: Int): Flow<ResumenRecoleccionRuta> =
        localDataSource.observarResumenDeRuta(rutaId).map { it.toDomain() }
}