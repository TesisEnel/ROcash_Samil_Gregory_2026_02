package ucne.edu.rocash.data.abonoDeuda.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ucne.edu.rocash.data.abonoDeuda.local.AbonoDeudaDao
import ucne.edu.rocash.data.abonoDeuda.mapper.toDomain
import ucne.edu.rocash.data.abonoDeuda.mapper.toEntity
import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda
import ucne.edu.rocash.domain.abonoDeuda.repository.AbonoDeudaRepository
import javax.inject.Inject

class AbonoDeudaRepositoryImpl @Inject constructor(
    private val dao: AbonoDeudaDao
) : AbonoDeudaRepository {

    override fun observarAbonosDeAgente(agenteId: Int): Flow<List<AbonoDeuda>> =
        dao.observarPorAgente(agenteId).map { lista -> lista.map { it.toDomain() } }

    override fun observarTotalAbonado(agenteId: Int): Flow<Double> =
        dao.observarTotalAbonado(agenteId)

    override suspend fun registrar(abono: AbonoDeuda): Int =
        dao.insertar(abono.toEntity()).toInt()
}
