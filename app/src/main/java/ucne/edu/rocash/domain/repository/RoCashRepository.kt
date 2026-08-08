package ucne.edu.rocash.domain.repository
import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.model.RegistroRecoleccion
import ucne.edu.rocash.domain.recolector.model.Recolector

interface RoCashRepository {
    fun obtenerHojaRutaActiva(recolectorId: String): Flow<HojaRuta?>
    fun obtenerEstacionesPorRuta(rutaId: String): Flow<List<EstacionVentas>>
    fun obtenerTodasLasEstaciones(): Flow<List<EstacionVentas>>
    suspend fun asignarRutaAEstacion(estacionId: String, rutaId: String)
    suspend fun guardarRegistroRecoleccion(registro: RegistroRecoleccion)
    suspend fun cerrarHojaRuta(hojaRutaId: String)
    suspend fun guardarHojaRuta(hojaRuta: HojaRuta)
    suspend fun guardarEstacion(estacion: EstacionVentas)

    suspend fun observeAllEstaciones(): Flow<List<EstacionVentas>>
}