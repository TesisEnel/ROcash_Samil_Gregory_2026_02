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
fun validateAgentesDistintos(agenteId: Int?, agenteId2: Int?): ValidationResult = when {
    agenteId2 == null -> ValidationResult(true)
    agenteId2 == agenteId ->
        ValidationResult(false, "El segundo agente debe ser distinto del titular")
    else -> ValidationResult(true)
}

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
