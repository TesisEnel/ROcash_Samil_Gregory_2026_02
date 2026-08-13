package ucne.edu.rocash.domain.registroRecoleccion.usecase

data class ValidationResult(
    val isValid: Boolean,
    val error: String? = null
)

fun validateMontoNumerico(valor: String, nombreCampo: String): ValidationResult {
    return when {
        valor.isBlank() -> ValidationResult(false, "El campo $nombreCampo es obligatorio")
        valor.toDoubleOrNull() == null -> ValidationResult(false, "El valor debe ser numérico")
        valor.toDouble() < 0 -> ValidationResult(false, "El valor no puede ser negativo")
        else -> ValidationResult(true)
    }
}