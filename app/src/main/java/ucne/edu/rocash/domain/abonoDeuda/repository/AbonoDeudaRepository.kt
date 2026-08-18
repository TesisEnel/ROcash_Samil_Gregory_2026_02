package ucne.edu.rocash.domain.abonoDeuda.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda

interface AbonoDeudaRepository {
    fun observarAbonosDeAgente(agenteId: Int): Flow<List<AbonoDeuda>>
    fun observarTotalAbonado(agenteId: Int): Flow<Double>
    suspend fun registrar(abono: AbonoDeuda): Int
}
