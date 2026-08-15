package ucne.edu.rocash.domain.hojaRuta.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import javax.inject.Inject

class GetHistorialRutasUseCase @Inject constructor(
    private val repository: HojaRutaRepository
) {
    operator fun invoke(recolectorId: String): Flow<List<HojaRuta>> =
        repository.observarHistorial(recolectorId)
}