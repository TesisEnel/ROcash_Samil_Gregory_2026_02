package ucne.edu.rocash.presentation.hojaRuta.detalle

import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.registroRecoleccion.model.ResumenRecoleccionRuta

/**
 * Snapshot inerte del detalle de una hoja de ruta.
 *
 * Antes este UiState resolvía dos cosas por su cuenta:
 *
 *   val puedeCerrarse get() = ruta?.puedeCerrarse == true && !isCerrando
 *   val estacionesPendientes get() = (ruta?.cantidadEstaciones ?: 0) - (ruta?.estacionesCuadradas ?: 0)
 *
 * La segunda era una resta de reglas de negocio hecha en la capa de UI, y encima
 * reimplementaba algo que el modelo de dominio podía contar directamente. La
 * primera mezclaba una regla de dominio (`HojaRuta.puedeCerrarse`) con un estado
 * de presentación (`isCerrando`) sin que quedara claro dónde termina una y
 * empieza la otra. Ahora la regla vive en `HojaRuta` y la mezcla la hace el
 * reducer.
 *
 * [cierreCompletado] y [errorMessage] son las banderas de una sola vez que la
 * pantalla consume con `LaunchedEffect` y apaga con su evento correspondiente,
 * siguiendo el patrón del Survival Guide. Ojo con no confundir
 * [cierreCompletado] (ocurrió el cierre en esta sesión, hay que navegar) con
 * [rutaEstaCerrada] (la ruta ya venía cerrada de la base de datos).
 */
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
