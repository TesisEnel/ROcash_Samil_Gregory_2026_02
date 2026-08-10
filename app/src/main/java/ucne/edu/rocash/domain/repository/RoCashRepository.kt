package ucne.edu.rocash.domain.repository
import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.model.RegistroRecoleccion
import ucne.edu.rocash.domain.recolector.model.Recolector

interface RoCashRepository {
    suspend fun guardarRegistroRecoleccion(registro: RegistroRecoleccion)
    suspend fun cerrarHojaRuta(hojaRutaId: String)

}