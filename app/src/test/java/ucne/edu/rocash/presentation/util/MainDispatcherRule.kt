package ucne.edu.rocash.presentation.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Sustituye Dispatchers.Main por un dispatcher de prueba durante cada test.
 *
 * Sin esto, cualquier `viewModelScope.launch { }` revienta en un test de JVM
 * porque no existe el looper de Android. Es la misma regla que trae el
 * Survival Guide en su TaskListViewModelTest, extraída a un archivo propio para
 * poder reutilizarla en todos los tests de ViewModel.
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
