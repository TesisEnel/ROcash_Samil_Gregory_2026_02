package ucne.edu.rocash.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.estacion.mapper.toDomain
import ucne.edu.rocash.data.local.dao.RoCashDao
import ucne.edu.rocash.data.mapper.toEntity
import ucne.edu.rocash.data.recolector.local.RecolectorDao

import ucne.edu.rocash.domain.model.RegistroRecoleccion
import ucne.edu.rocash.domain.recolector.model.Recolector
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

class RoCashRepositoryImpl @Inject constructor(
    private val dao: RoCashDao
) : RoCashRepository {

    override suspend fun guardarRegistroRecoleccion(registro: RegistroRecoleccion) {
        dao.insertarRegistroRecoleccion(registro.toEntity())
    }

    override suspend fun cerrarHojaRuta(hojaRutaId: String) {
        dao.cerrarHojaRuta(hojaRutaId)
    }

}