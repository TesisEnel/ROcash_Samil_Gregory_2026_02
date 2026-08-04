package ucne.edu.rocash.domain.usecase

import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject

class CrearHojaRutaUseCase @Inject constructor(
    private val repository: RoCashRepository
) {
    suspend operator fun invoke(hojaRuta: HojaRuta) {
        repository.insertarHojaRuta(hojaRuta)
    }
}