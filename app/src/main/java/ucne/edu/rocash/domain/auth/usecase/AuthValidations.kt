package ucne.edu.rocash.domain.auth.usecase

import android.util.Patterns
data class ValidationResult(
    val isValid: Boolean,
    val error: String? = null
)

fun validateEmail(email: String): ValidationResult {
    return when {
        email.isBlank() -> ValidationResult(false, "El correo electrónico no puede estar vacío")
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(false, "El formato del correo es inválido")
        else -> ValidationResult(true)
    }
}

fun validatePassword(password: String): ValidationResult {
    return when {
        password.isBlank() -> ValidationResult(false, "La contraseña no puede estar vacía")
        password.length < 6 -> ValidationResult(false, "La contraseña debe tener al menos 6 caracteres")
        else -> ValidationResult(true)
    }
}