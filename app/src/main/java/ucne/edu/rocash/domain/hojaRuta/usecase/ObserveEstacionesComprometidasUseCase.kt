package ucne.edu.rocash.domain.hojaRuta.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import javax.inject.Inject

class ObserveEstacionesComprometidasUseCase @Inject constructor(
    private val repository: HojaRutaRepository
) {
    operator fun invoke(): Flow<Set<Int>> = repository.observarEstacionesComprometidas()
}
