package ucne.edu.rocash.domain.repository
import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.model.EstacionVentas
import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.model.RegistroRecoleccion

interface RoCashRepository {
    fun obtenerHojaRutaActiva(recolectorId: Int): Flow<HojaRuta?>
    fun obtenerEstacionesPorRuta(hojaRutaId: Int): Flow<List<EstacionVentas>>
    suspend fun guardarRegistroRecoleccion(registro: RegistroRecoleccion)
    suspend fun actualizarDeudaAgente(agenteId: Int, nuevaDeuda: Double)
    suspend fun cerrarHojaRuta(hojaRutaId: Int)
}