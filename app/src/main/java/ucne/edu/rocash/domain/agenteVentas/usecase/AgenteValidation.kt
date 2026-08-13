package ucne.edu.rocash.domain.agenteVentas.usecase

data class ValidationResult(
    val isValid: Boolean,
    val error: String? = null
)

fun validateNombre(nombre: String): ValidationResult {
    return when {
        nombre.isBlank() -> ValidationResult(false, "El nombre no puede estar vacío")
        nombre.length < 3 -> ValidationResult(false, "El nombre debe tener al menos 3 caracteres")
        else -> ValidationResult(true)
    }
}

fun validateTelefono(telefono: String): ValidationResult {
    return when {
        telefono.isBlank() -> ValidationResult(false, "El teléfono no puede estar vacío")
        telefono.length < 10 -> ValidationResult(false, "El teléfono no parece válido")
        else -> ValidationResult(true)
    }
}