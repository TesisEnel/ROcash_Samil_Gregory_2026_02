package ucne.edu.rocash.domain.registroRecoleccion.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.registroRecoleccion.model.RegistroRecoleccion
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta

interface RegistroRecoleccionRepository {
    fun observeRecolecciones(): Flow<List<RegistroRecoleccion>>
    suspend fun getRecoleccion(id: Int): RegistroRecoleccion?
    suspend fun upsert(registro: RegistroRecoleccion): Int
    suspend fun delete(id: Int)
    suspend fun exists(id: Int): Boolean

    fun observarPorRuta(rutaId: Int): Flow<List<RegistroRecoleccion>>
    suspend fun obtenerPorRutaYEstacion(rutaId: Int, estacionId: Int): RegistroRecoleccion?

    suspend fun obtenerResumenDeRuta(rutaId: Int): ResumenRecoleccionRuta
    fun observarResumenDeRuta(rutaId: Int): Flow<ResumenRecoleccionRuta>
}
