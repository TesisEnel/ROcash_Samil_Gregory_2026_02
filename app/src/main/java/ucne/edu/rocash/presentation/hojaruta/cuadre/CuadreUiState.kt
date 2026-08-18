package ucne.edu.rocash.presentation.hojaRuta.cuadre

/**
 * Snapshot inerte del formulario de cuadre.
 *
 * Antes este UiState resolvía por su cuenta:
 *
 *   val puedeGuardar: Boolean
 *       get() = !isSaving && !isLoading &&
 *               ventaBruta.isNotBlank() &&
 *               comisionCliente.isNotBlank() &&
 *               montoRecolectado.isNotBlank()
 *
 * Eran cinco condiciones de habilitación evaluándose en cada recomposición, y
 * era la UI la que decidía cuándo un cuadre está listo para guardarse. Ahora esa
 * decisión la toma el ViewModel una sola vez por transición y aquí sólo queda el
 * booleano ya resuelto.
 *
 * [saved] y [errorMessage] siguen siendo banderas de una sola vez dentro del
 * estado, consumidas con `LaunchedEffect` y apagadas con su evento
 * correspondiente: es el patrón del Survival Guide y se mantiene por
 * consistencia con el resto del curso.
 *
 * [montoEsperado] y [deudaGenerada] siguen siendo campos, pero ya no los calcula
 * el ViewModel a mano: llegan desde `CalculoCuadre` en el dominio, que es el
 * único dueño de la fórmula del cuadre.
 */
data class CuadreUiState(
    val hojaRutaId: Int = 0,
    val estacionId: Int = 0,
    val agenteId: Int = 0,
    val agenteId2: Int? = null,
    val nombreEstacion: String = "",

    val ventaBruta: String = "",
    val comisionCliente: String = "",
    val montoRecolectado: String = "",
    val notaIncidencia: String = "",

    val montoEsperado: Double = 0.0,
    val deudaGenerada: Double = 0.0,
    val hayDeuda: Boolean = false,
    val deudaSeReparte: Boolean = false,

    val ventaBrutaError: String? = null,
    val comisionError: String? = null,
    val montoRecolectadoError: String? = null,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isNew: Boolean = true,
    val puedeGuardar: Boolean = false,

    val mostrarDialogoConfirmacion: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null
)
