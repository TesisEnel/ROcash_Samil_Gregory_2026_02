package ucne.edu.rocash.domain.estacion.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

interface EstacionRepository {
    fun observeEstaciones(): Flow<List<EstacionVentas>>
    suspend fun getEstacion(id: Int): EstacionVentas?
    suspend fun upsert(estacion: EstacionVentas): Int
    suspend fun delete(id: Int)
    suspend fun exists(id: Int): Boolean

    suspend fun bancasDelAgente(agenteId: Int, estacionIdExcluida: Int): List<String>
    fun buscarEstaciones(query: String): Flow<List<EstacionVentas>>
}
