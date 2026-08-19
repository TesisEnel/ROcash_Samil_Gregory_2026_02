package ucne.edu.rocash.domain.abonoDeuda.usecase

import ucne.edu.rocash.domain.common.ValidationResult

fun validateMontoAbono(valor: String, deudaActual: Double): ValidationResult = when {
    valor.isBlank() -> ValidationResult(false, "Escribe cuánto está abonando")
    valor.toDoubleOrNull() == null -> ValidationResult(false, "El valor debe ser numérico")
    valor.toDouble() <= 0.0 -> ValidationResult(false, "El abono debe ser mayor que cero")
    deudaActual <= 0.0 -> ValidationResult(false, "Este agente no tiene deuda pendiente")
    valor.toDouble() > deudaActual ->
        ValidationResult(false, "El abono supera la deuda; usa Saldar para dejarla en cero")
    else -> ValidationResult(true)
}
