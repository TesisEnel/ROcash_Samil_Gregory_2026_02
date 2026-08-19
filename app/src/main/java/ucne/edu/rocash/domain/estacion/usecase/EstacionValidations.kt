package ucne.edu.rocash.domain.estacion.usecase

import ucne.edu.rocash.domain.common.ValidationResult

fun validateEstacionNombre(nombre: String): ValidationResult {
    return when {
        nombre.isBlank() -> ValidationResult(false, "El nombre de la estación no puede estar vacío")
        nombre.length < 3 -> ValidationResult(false, "El nombre debe tener al menos 3 caracteres")
        else -> ValidationResult(true)
    }
}

fun validateEstacionDireccion(direccion: String): ValidationResult {
    return when {
        direccion.isBlank() -> ValidationResult(false, "La dirección no puede estar vacía")
        else -> ValidationResult(true)
    }
}

fun validateAgenteAsignado(agenteId: Int?): ValidationResult {
    return when {
        agenteId == null || agenteId == 0 -> ValidationResult(false, "Debe seleccionar un agente")
        else -> ValidationResult(true)
    }
}
/**
 * Un agente no puede figurar dos veces en la misma banca.
 *
 * Sin esta regla se podía poner al mismo agente como titular y como segundo.
 * El cuadre reparte la deuda por mitades entre los dos, así que al mismo agente
 * se le hacían dos cargos de la mitad: el total salía bien de casualidad, pero
 * el historial quedaba con dos movimientos donde debía haber uno.
 */
fun validateAgentesDistintos(agenteId: Int?, agenteId2: Int?): ValidationResult = when {
    agenteId2 == null -> ValidationResult(true)
    agenteId2 == agenteId ->
        ValidationResult(false, "El segundo agente debe ser distinto del titular")
    else -> ValidationResult(true)
}

/**
 * Un agente pertenece a una sola banca.
 *
 * La deuda se acumula por agente, no por banca. Si el mismo agente atendiera
 * dos bancas, su saldo mezclaría faltantes de ambas y sería imposible saber
 * cuál origina qué, ni ante quién reclamar.
 */
fun validateAgenteLibre(
    nombreAgente: String,
    bancasDondeYaFigura: List<String>
): ValidationResult = when {
    bancasDondeYaFigura.isEmpty() -> ValidationResult(true)
    else -> ValidationResult(
        false,
        "$nombreAgente ya está asignado a: " + bancasDondeYaFigura.joinToString(", ")
    )
}
