package ucne.edu.rocash.domain.hojaRuta.usecase

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import javax.inject.Inject

class CrearHojaRutaUseCase @Inject constructor(
    private val repository: HojaRutaRepository
) {
    suspend operator fun invoke(ruta: HojaRuta): Int {
        return repository.insertarRuta(ruta)
    }
}