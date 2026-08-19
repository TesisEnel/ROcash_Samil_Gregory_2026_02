package ucne.edu.rocash.domain.hojaRuta.usecase

import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import javax.inject.Inject

class OmitirEstacionUseCase @Inject constructor(
    private val repository: HojaRutaRepository
) {
    suspend operator fun invoke(rutaId: Int, estacionId: Int): Result<Unit> = runCatching {
        val ruta = repository.obtenerRuta(rutaId)
            ?: throw IllegalStateException("La hoja de ruta no existe")

        if (ruta.estado == EstadoRuta.CERRADA) {
            throw IllegalStateException("La ruta ya está cerrada")
        }

        repository.marcarEstadoEstacion(rutaId, estacionId, EstadoVisitaEstacion.OMITIDA)

        if (ruta.estado == EstadoRuta.PENDIENTE) {
            repository.cambiarEstadoRuta(rutaId, EstadoRuta.EN_PROGRESO)
        }
    }
}
