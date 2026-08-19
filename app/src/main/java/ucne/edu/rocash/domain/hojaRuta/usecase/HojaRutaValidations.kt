package ucne.edu.rocash.domain.hojaRuta.usecase

import ucne.edu.rocash.domain.common.ValidationResult

const val MAX_ESTACIONES_POR_RUTA = 30

fun validateRecolectorId(recolectorId: String?): ValidationResult = when {
    recolectorId.isNullOrBlank() ->
        ValidationResult(false, "No hay un recolector autenticado")
    else -> ValidationResult(true)
}

fun validateEstacionesSeleccionadas(estacionIds: Collection<Int>): ValidationResult = when {
    estacionIds.isEmpty() ->
        ValidationResult(false, "Debe seleccionar al menos una estación")
    estacionIds.size > MAX_ESTACIONES_POR_RUTA ->
        ValidationResult(false, "Una ruta no puede tener más de $MAX_ESTACIONES_POR_RUTA estaciones")
    estacionIds.distinct().size != estacionIds.size ->
        ValidationResult(false, "Hay estaciones repetidas en la selección")
    else -> ValidationResult(true)
}

fun validateEstacionesLibres(comprometidas: List<Int>): ValidationResult = when {
    comprometidas.isEmpty() -> ValidationResult(true)
    else -> ValidationResult(
        false,
        "Estas estaciones ya pertenecen a una ruta abierta: " +
                comprometidas.joinToString(", ")
    )
}
