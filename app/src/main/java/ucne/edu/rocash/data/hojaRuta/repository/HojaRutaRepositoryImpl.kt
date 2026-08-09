package ucne.edu.rocash.data.hojaRuta.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaDao
import ucne.edu.rocash.data.hojaRuta.mapper.toDomain
import ucne.edu.rocash.data.hojaRuta.mapper.toEntity
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import javax.inject.Inject

class HojaRutaRepositoryImpl @Inject constructor(
    private val dao: HojaRutaDao
) : HojaRutaRepository {

    override suspend fun insertarRuta(ruta: HojaRuta): Int {
        return dao.insertarRuta(ruta.toEntity()).toInt()
    }

    override suspend fun actualizarRuta(ruta: HojaRuta) {
        dao.actualizarRuta(ruta.toEntity())
    }

    override fun obtenerRutaActiva(recolectorId: String): Flow<HojaRuta?> {
        return dao.obtenerRutaActiva(recolectorId).map { it?.toDomain() }
    }

    override fun obtenerHistorialRutas(recolectorId: String): Flow<List<HojaRuta>> {
        return dao.obtenerHistorial(recolectorId).map { lista -> lista.map { it.toDomain() } }
    }

    override fun obtenerTotalIngresos(recolectorId: String): Flow<Double> {
        return dao.obtenerTotalIngresos(recolectorId).map { it ?: 0.0 }
    }

    override fun obtenerTotalRutasCompletadas(recolectorId: String): Flow<Int> {
        return dao.obtenerTotalRutasCompletadas(recolectorId)
    }
}