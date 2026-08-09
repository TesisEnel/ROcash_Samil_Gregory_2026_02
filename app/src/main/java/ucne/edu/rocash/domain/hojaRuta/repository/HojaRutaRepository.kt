package ucne.edu.rocash.domain.hojaRuta.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta

interface RutaRepository {
    suspend fun insertarRuta(ruta: HojaRuta): Int
    suspend fun actualizarRuta(ruta: HojaRuta)

    fun obtenerRutaActiva(recolectorId: String): Flow<HojaRuta?>

    fun obtenerHistorialRutas(recolectorId: String): Flow<List<HojaRuta>>

    fun obtenerTotalIngresos(recolectorId: String): Flow<Double>
    fun obtenerTotalRutasCompletadas(recolectorId: String): Flow<Int>
}