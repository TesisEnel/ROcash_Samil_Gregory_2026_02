package ucne.edu.rocash.domain.hojaRuta.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import javax.inject.Inject

class GetRutaActivaUseCase @Inject constructor(
    private val repository: HojaRutaRepository
) {
    operator fun invoke(recolectorId: String): Flow<HojaRuta?> {
        return repository.obtenerRutaActiva(recolectorId)
    }
}