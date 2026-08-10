package ucne.edu.rocash.domain.estacion.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.estacion.model.EstacionVentas

interface EstacionRepository {
    suspend fun insertarEstacion(estacion: EstacionVentas)
    fun obtenerEstaciones(): Flow<List<EstacionVentas>>
    suspend fun obtenerEstacionPorId(id: String): EstacionVentas?
    fun buscarEstaciones(query: String): Flow<List<EstacionVentas>>

    suspend fun asignarRuta(estacionId: String, rutaId: Int)
}