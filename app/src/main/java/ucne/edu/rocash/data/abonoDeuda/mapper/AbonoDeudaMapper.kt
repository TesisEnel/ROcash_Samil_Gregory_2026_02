package ucne.edu.rocash.data.abonoDeuda.mapper

import ucne.edu.rocash.data.abonoDeuda.local.AbonoDeudaEntity
import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda

fun AbonoDeudaEntity.toDomain(): AbonoDeuda = AbonoDeuda(
    abonoId = abonoId,
    agenteId = agenteId,
    monto = monto,
    deudaAntes = deudaAntes,
    deudaDespues = deudaDespues,
    fecha = fecha,
    nota = nota
)

fun AbonoDeuda.toEntity(): AbonoDeudaEntity = AbonoDeudaEntity(
    abonoId = abonoId,
    agenteId = agenteId,
    monto = monto,
    deudaAntes = deudaAntes,
    deudaDespues = deudaDespues,
    fecha = fecha,
    nota = nota
)
