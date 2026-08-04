package ucne.edu.rocash.domain.repository
import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.model.EstacionVentas
import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.model.RegistroRecoleccion

interface RoCashRepository {
    fun obtenerHojaRutaActiva(recolectorId: String): Flow<HojaRuta?>
    fun obtenerEstacionesPorRuta(hojaRutaId: String): Flow<List<EstacionVentas>>
    suspend fun guardarRegistroRecoleccion(registro: RegistroRecoleccion)
    suspend fun actualizarDeudaAgente(agenteId: String, nuevaDeuda: Double)
    suspend fun cerrarHojaRuta(hojaRutaId: String)

    suspend fun insertarHojaRuta(hojaRuta: HojaRuta)

    suspend fun insertarEstacion(estacion: EstacionVentas)
}