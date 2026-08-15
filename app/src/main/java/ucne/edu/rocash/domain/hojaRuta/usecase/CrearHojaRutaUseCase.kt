package ucne.edu.rocash.domain.hojaRuta.usecase

import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import javax.inject.Inject

class CrearHojaRutaUseCase @Inject constructor(
    private val repository: HojaRutaRepository
) {
    suspend operator fun invoke(
        recolectorId: String?,
        estacionIds: List<Int>
    ): Result<Int> {
        val recolectorResult = validateRecolectorId(recolectorId)
        if (!recolectorResult.isValid) {
            return Result.failure(IllegalArgumentException(recolectorResult.error))
        }

        val seleccionResult = validateEstacionesSeleccionadas(estacionIds)
        if (!seleccionResult.isValid) {
            return Result.failure(IllegalArgumentException(seleccionResult.error))
        }

        return runCatching {
            val comprometidas = repository.estacionesYaComprometidas(estacionIds)
            val libresResult = validateEstacionesLibres(comprometidas)
            if (!libresResult.isValid) {
                throw IllegalStateException(libresResult.error)
            }

            val nuevaRuta = HojaRuta(
                recolectorId = recolectorId!!,
                fechaCreacion = System.currentTimeMillis(),
                estado = EstadoRuta.PENDIENTE
            )

            repository.crearRutaConEstaciones(nuevaRuta, estacionIds)
        }
    }
}