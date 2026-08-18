package ucne.edu.rocash.presentation.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Base para todos los ViewModel de RoCash.
 *
 * Fija el ciclo MVI unidireccional:
 *
 *      UiEvent  ->  onEvent()  ->  reduce { }  ->  StateFlow<UiState>  ->  UI
 *
 * No agrega nada a la superficie pública que el Survival Guide define para un
 * ViewModel: sigue exponiendo `state: StateFlow<S>` y `onEvent(E)`, igual que
 * `TaskListViewModel`. Lo único que aporta es evitar repetir el par
 * `_state`/`state` en cada pantalla y encauzar las transiciones por [reduce],
 * que es donde se invocan los reducers puros.
 */
abstract class MviViewModel<S : UiState, E : UiEvent>(
    estadoInicial: S
) : ViewModel() {

    private val _state = MutableStateFlow(estadoInicial)
    val state: StateFlow<S> = _state.asStateFlow()

    /** Lectura puntual del estado actual, para decisiones dentro del ViewModel. */
    protected val estadoActual: S get() = _state.value

    /** Única entrada de intenciones desde la UI. */
    abstract fun onEvent(event: E)

    /**
     * Aplica una transición pura sobre el estado. El reducer recibe el estado
     * anterior y devuelve el nuevo; nunca debe lanzar corrutinas ni tocar IO.
     */
    protected fun reduce(reducer: (S) -> S) {
        _state.update(reducer)
    }
}
