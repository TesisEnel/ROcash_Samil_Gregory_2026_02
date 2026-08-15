package ucne.edu.rocash.data.hojaRuta.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.hojaRuta.local.HojaRutaDao
import ucne.edu.rocash.data.hojaRuta.mapper.toDomain
import ucne.edu.rocash.data.hojaRuta.mapper.toEntity
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import javax.inject.Inject

class HojaRutaRepositoryImpl @Inject constructor(
    private val localDataSource: HojaRutaDao
) : HojaRutaRepository {

    override suspend fun crearRutaConEstaciones(ruta: HojaRuta, estacionIds: List<Int>): Int {
        return localDataSource.crearRutaConEstaciones(
            ruta = ruta.toEntity(),
            estacionIds = estacionIds,
            estadoInicialEstacion = EstadoVisitaEstacion.PENDIENTE.name
        )
    }

    override suspend fun actualizarRuta(ruta: HojaRuta) {
        localDataSource.actualizar(ruta.toEntity())
    }

    override suspend fun cambiarEstadoRuta(rutaId: Int, estado: EstadoRuta) {
        localDataSource.cambiarEstado(rutaId, estado.name)
    }

    override suspend fun marcarEstadoEstacion(
        rutaId: Int,
        estacionId: Int,
        estado: EstadoVisitaEstacion
    ) {
        localDataSource.marcarEstadoEstacion(rutaId, estacionId, estado.name)
    }

    override suspend fun cerrarRuta(
        rutaId: Int,
        fechaCierre: Long,
        totalVentaBruta: Double,
        totalComisionClientes: Double,
        totalRecaudado: Double,
        totalDeudas: Double
    ) {
        localDataSource.cerrarConTotales(
            rutaId = rutaId,
            estadoCerrada = EstadoRuta.CERRADA.name,
            fechaCierre = fechaCierre,
            totalVentaBruta = totalVentaBruta,
            totalComisionClientes = totalComisionClientes,
            totalRecaudado = totalRecaudado,
            totalDeudas = totalDeudas
        )
    }

    override fun observarRuta(rutaId: Int): Flow<HojaRuta?> =
        localDataSource.observarRutaConEstaciones(rutaId).map { it?.toDomain() }

    override suspend fun obtenerRuta(rutaId: Int): HojaRuta? =
        localDataSource.obtenerRutaConEstaciones(rutaId)?.toDomain()

    override fun observarRutasAbiertas(recolectorId: String): Flow<List<HojaRuta>> =
        localDataSource
            .observarRutasAbiertas(recolectorId, EstadoRuta.NOMBRES_ABIERTOS)
            .map { lista -> lista.map { it.toDomain() } }

    override fun observarHistorial(recolectorId: String): Flow<List<HojaRuta>> =
        localDataSource
            .observarHistorial(recolectorId, EstadoRuta.CERRADA.name)
            .map { lista -> lista.map { it.toDomain() } }

    override fun observarTotalIngresos(recolectorId: String): Flow<Double> =
        localDataSource.observarTotalIngresos(recolectorId, EstadoRuta.CERRADA.name)

    override fun observarTotalRutasCompletadas(recolectorId: String): Flow<Int> =
        localDataSource.observarTotalRutasCompletadas(recolectorId, EstadoRuta.CERRADA.name)

    override suspend fun estacionesYaComprometidas(estacionIds: List<Int>): List<Int> {
        if (estacionIds.isEmpty()) return emptyList()
        return localDataSource.estacionesYaComprometidas(
            estacionIds = estacionIds,
            estadosAbiertos = EstadoRuta.NOMBRES_ABIERTOS
        )
    }

    override fun observarEstacionesComprometidas(): Flow<Set<Int>> =
        localDataSource
            .observarEstacionesComprometidas(EstadoRuta.NOMBRES_ABIERTOS)
            .map { it.toSet() }
}