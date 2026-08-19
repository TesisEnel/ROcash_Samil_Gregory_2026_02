package ucne.edu.rocash.presentation.hojaRuta.detalle

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta
data class DetalleRutaUiState(
    val rutaId: Int = 0,
    val ruta: HojaRuta? = null,
    val resumen: ResumenRecoleccionRuta = ResumenRecoleccionRuta(),

    val puedeCerrarse: Boolean = false,
    val estacionesPendientes: Int = 0,
    val hayEstacionesPendientes: Boolean = false,
    val rutaEstaCerrada: Boolean = false,
    val mostrarAccionCierre: Boolean = false,

    val isLoading: Boolean = true,
    val isCerrando: Boolean = false,
    val mostrarDialogoCierre: Boolean = false,
    val noEncontrada: Boolean = false,

    val cierreCompletado: Boolean = false,
    val errorMessage: String? = null
)
