package ucne.edu.rocash.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.local.dao.RoCashDao
import ucne.edu.rocash.data.mapper.toDomain
import ucne.edu.rocash.data.mapper.toEntity
import ucne.edu.rocash.data.recolector.local.RecolectorDao
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.model.RegistroRecoleccion
import ucne.edu.rocash.domain.recolector.model.Recolector
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

class RoCashRepositoryImpl @Inject constructor(
    private val dao: RoCashDao
) : RoCashRepository {

    override fun obtenerHojaRutaActiva(recolectorId: String): Flow<HojaRuta?> {
        return dao.obtenerHojaRutaActiva(recolectorId).map { it?.toDomain() }
    }

    override fun obtenerEstacionesPorRuta(rutaId: String): Flow<List<EstacionVentas>> {
        return dao.obtenerEstacionesPorRuta(rutaId).map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override fun obtenerTodasLasEstaciones(): Flow<List<EstacionVentas>> {
        return dao.obtenerTodasLasEstaciones().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun asignarRutaAEstacion(estacionId: String, rutaId: String) {
        dao.asignarRutaAEstacion(estacionId, rutaId)
    }

    override suspend fun guardarRegistroRecoleccion(registro: RegistroRecoleccion) {
        dao.insertarRegistroRecoleccion(registro.toEntity())
    }

    override suspend fun cerrarHojaRuta(hojaRutaId: String) {
        dao.cerrarHojaRuta(hojaRutaId)
    }

    override suspend fun guardarHojaRuta(hojaRuta: HojaRuta) {
        dao.insertarHojaRuta(hojaRuta.toEntity())
    }

    override suspend fun guardarEstacion(estacion: EstacionVentas) {
        dao.insertarEstacion(estacion.toEntity())
    }

    override suspend fun observeAllEstaciones(): Flow<List<EstacionVentas>> {
        return dao.observeAllEstaciones().map { lista -> lista.map { it.toDomain() } }
    }
}