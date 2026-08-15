package ucne.edu.rocash.domain.registroRecoleccion.usecase

import ucne.edu.rocash.domain.common.ValidationResult

fun validateMontoNumerico(valor: String, nombreCampo: String): ValidationResult = when {
    valor.isBlank() -> ValidationResult(false, "El campo $nombreCampo es obligatorio")
    valor.toDoubleOrNull() == null -> ValidationResult(false, "El valor debe ser numérico")
    valor.toDouble() < 0 -> ValidationResult(false, "El valor no puede ser negativo")
    else -> ValidationResult(true)
}

fun validateCoherenciaCuadre(
    ventaBruta: Double,
    comisionCliente: Double,
    montoRecolectado: Double
): ValidationResult = when {
    comisionCliente > ventaBruta ->
        ValidationResult(false, "La comisión no puede ser mayor que la venta bruta")
    montoRecolectado > ventaBruta - comisionCliente ->
        ValidationResult(false, "El monto recolectado supera lo esperado para esta banca")
    else -> ValidationResult(true)
}