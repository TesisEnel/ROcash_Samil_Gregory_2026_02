package ucne.edu.rocash.domain.hojaRuta.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

interface HojaRutaRepository {

    suspend fun crearRutaConEstaciones(ruta: HojaRuta, estacionIds: List<Int>): Int

    suspend fun actualizarRuta(ruta: HojaRuta)

    suspend fun cambiarEstadoRuta(rutaId: Int, estado: EstadoRuta)

    suspend fun marcarEstadoEstacion(
        rutaId: Int,
        estacionId: Int,
        estado: EstadoVisitaEstacion
    )

    suspend fun cerrarRuta(
        rutaId: Int,
        fechaCierre: Long,
        totalVentaBruta: Double,
        totalComisionClientes: Double,
        totalRecaudado: Double,
        totalDeudas: Double
    )

    fun observarRuta(rutaId: Int): Flow<HojaRuta?>

    suspend fun obtenerRuta(rutaId: Int): HojaRuta?

    fun observarRutasAbiertas(recolectorId: String): Flow<List<HojaRuta>>

    fun observarHistorial(recolectorId: String): Flow<List<HojaRuta>>

    fun observarTotalIngresos(recolectorId: String): Flow<Double>

    fun observarTotalRutasCompletadas(recolectorId: String): Flow<Int>

    suspend fun estacionesYaComprometidas(estacionIds: List<Int>): List<Int>

    fun observarEstacionesComprometidas(): Flow<Set<Int>>
}