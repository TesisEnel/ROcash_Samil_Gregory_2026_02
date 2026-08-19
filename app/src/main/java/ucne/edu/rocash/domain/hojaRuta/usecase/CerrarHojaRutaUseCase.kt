package ucne.edu.rocash.domain.hojaRuta.usecase

import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.EstadoVisitaEstacion
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.repository.HojaRutaRepository
import ucne.edu.rocash.domain.registroRecoleccion.repository.RegistroRecoleccionRepository
import javax.inject.Inject

class CerrarHojaRutaUseCase @Inject constructor(
    private val hojaRutaRepository: HojaRutaRepository,
    private val registroRepository: RegistroRecoleccionRepository
) {
    suspend operator fun invoke(rutaId: Int): Result<HojaRuta> = runCatching {
        val ruta = hojaRutaRepository.obtenerRuta(rutaId)
            ?: throw IllegalStateException("La hoja de ruta no existe")

        if (ruta.estado == EstadoRuta.CERRADA) {
            throw IllegalStateException("Esta ruta ya fue cerrada")
        }

        if (ruta.estaciones.isEmpty()) {
            throw IllegalStateException("La ruta no tiene estaciones asignadas")
        }

        val pendientes = ruta.estaciones.filter {
            it.estado == EstadoVisitaEstacion.PENDIENTE
        }
        if (pendientes.isNotEmpty()) {
            throw IllegalStateException(
                "Faltan ${pendientes.size} estaciones por cuadrar: " +
                        pendientes.joinToString(", ") { it.nombre }
            )
        }

        val resumen = registroRepository.obtenerResumenDeRuta(rutaId)
        val fechaCierre = System.currentTimeMillis()

        hojaRutaRepository.cerrarRuta(
            rutaId = rutaId,
            fechaCierre = fechaCierre,
            totalVentaBruta = resumen.totalVentaBruta,
            totalComisionClientes = resumen.totalComisionClientes,
            totalRecaudado = resumen.totalRecaudado,
            totalDeudas = resumen.totalDeudas
        )

        ruta.copy(
            estado = EstadoRuta.CERRADA,
            fechaCierre = fechaCierre,
            totalVentaBruta = resumen.totalVentaBruta,
            totalComisionClientes = resumen.totalComisionClientes,
            totalRecaudado = resumen.totalRecaudado,
            totalDeudas = resumen.totalDeudas
        )
    }
}
