package ucne.edu.rocash.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.local.dao.RoCashDao
import ucne.edu.rocash.data.mapper.toDomain
import ucne.edu.rocash.data.mapper.toEntity
import ucne.edu.rocash.domain.model.EstacionVentas
import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.model.RegistroRecoleccion
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

class RoCashRepositoryImpl @Inject constructor(
    private val dao: RoCashDao
) : RoCashRepository {
    override fun obtenerHojaRutaActiva(recolectorId: String): Flow<HojaRuta?> {
        return dao.obtenerHojaRutaActiva(recolectorId).map { it?.toDomain() }
    }

    override fun obtenerEstacionesPorRuta(hojaRutaId: String): Flow<List<EstacionVentas>> {
        return dao.obtenerTodasLasEstaciones().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun guardarRegistroRecoleccion(registro: RegistroRecoleccion) {
        dao.insertarRegistroRecoleccion(registro.toEntity())
    }

    override suspend fun actualizarDeudaAgente(agenteId: String, nuevaDeuda: Double) {
        dao.sumarDeudaAgente(agenteId, nuevaDeuda)
    }

    override suspend fun cerrarHojaRuta(hojaRutaId: String) {
        dao.cerrarHojaRuta(hojaRutaId)

    }
}