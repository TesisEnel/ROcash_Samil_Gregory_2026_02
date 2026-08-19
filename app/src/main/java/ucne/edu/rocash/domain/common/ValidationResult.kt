package ucne.edu.rocash.domain.common

data class ValidationResult(
    val isValid: Boolean,
    val error: String? = null
)
