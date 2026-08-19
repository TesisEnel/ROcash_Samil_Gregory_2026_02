package ucne.edu.rocash.domain.agenteVentas.usecase

import ucne.edu.rocash.domain.common.ValidationResult
fun validateNombre(nombre: String): ValidationResult = when {
    nombre.isBlank() -> ValidationResult(false, "El nombre no puede estar vacío")
    nombre.length < 3 -> ValidationResult(false, "El nombre debe tener al menos 3 caracteres")
    else -> ValidationResult(true)
}

fun validateTelefono(telefono: String): ValidationResult = when {
    telefono.isBlank() -> ValidationResult(false, "El teléfono no puede estar vacío")
    telefono.length < 10 -> ValidationResult(false, "El teléfono no parece válido")
    else -> ValidationResult(true)
}
