package ucne.edu.rocash.domain.estacion.usecase

data class ValidationResult(
    val isValid: Boolean,
    val error: String? = null
)

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