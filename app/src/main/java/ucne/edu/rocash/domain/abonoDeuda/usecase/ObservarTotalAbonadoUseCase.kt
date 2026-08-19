package ucne.edu.rocash.domain.abonoDeuda.usecase

import kotlinx.coroutines.flow.Flow
import ucne.edu.rocash.domain.abonoDeuda.repository.AbonoDeudaRepository
import javax.inject.Inject

/**
 * Total abonado por un agente.
 *
 * Existe para que la suma no se haga recorriendo la lista en el ViewModel. Es
 * la misma corrección que se le hizo a `HistorialRutasUiState`: acumular dinero
 * es una regla de negocio y se resuelve con SUM() en SQLite, no con un sumOf en
 * la capa de presentación.
 */
class ObservarTotalAbonadoUseCase @Inject constructor(
    private val repository: AbonoDeudaRepository
) {
    operator fun invoke(agenteId: Int): Flow<Double> = repository.observarTotalAbonado(agenteId)
}
