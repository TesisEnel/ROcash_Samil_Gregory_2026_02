package ucne.edu.rocash.domain.registroRecoleccion.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion


interface RegistroRecoleccionRepository {
    fun observeRecolecciones(): Flow<List<RegistroRecoleccion>>
    suspend fun getRecoleccion(id: Int): RegistroRecoleccion?
    suspend fun upsert(registro: RegistroRecoleccion): Int
    suspend fun delete(id: Int)
    suspend fun exists(id: Int): Boolean
}