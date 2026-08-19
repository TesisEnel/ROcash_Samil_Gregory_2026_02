package ucne.edu.rocash.domain.hojaRuta.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import javax.inject.Inject

class GetTotalIngresosUseCase @Inject constructor(
    private val repository: HojaRutaRepository
) {
    operator fun invoke(recolectorId: String): Flow<Double> =
        repository.observarTotalIngresos(recolectorId)
}
