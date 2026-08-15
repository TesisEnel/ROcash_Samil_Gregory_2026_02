package ucne.edu.rocash.presentation.hojaRuta.detalle

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta

data class DetalleRutaUiState(
    val rutaId: Int = 0,
    val ruta: HojaRuta? = null,
    val resumen: ResumenRecoleccionRuta = ResumenRecoleccionRuta(),
    val isLoading: Boolean = true,
    val isCerrando: Boolean = false,
    val mostrarDialogoCierre: Boolean = false,
    val rutaCerrada: Boolean = false,
    val noEncontrada: Boolean = false,
    val errorMessage: String? = null
) {
    val puedeCerrarse: Boolean get() = ruta?.puedeCerrarse == true && !isCerrando

    val estacionesPendientes: Int
        get() = (ruta?.cantidadEstaciones ?: 0) - (ruta?.estacionesCuadradas ?: 0)
}