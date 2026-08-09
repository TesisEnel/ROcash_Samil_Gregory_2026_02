package ucne.edu.rocash.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import ucne.edu.rocash.domain.hojaRuta.usecase.GetRutaActivaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.GetTotalIngresosUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.GetTotalRutasCompletadasUseCase

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRutaActivaUseCase: GetRutaActivaUseCase,
    private val getTotalIngresosUseCase: GetTotalIngresosUseCase,
    private val getTotalRutasCompletadasUseCase: GetTotalRutasCompletadasUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUIState())
    val state: StateFlow<HomeUIState> = _state.asStateFlow()

    init {
        processIntent(HomeUIEvent.CargarDatos)
    }

    fun processIntent(intent: HomeUIEvent) {
        when (intent) {
            is HomeUIEvent.CargarDatos -> cargarDatosRecolector()
        }
    }

    private fun cargarDatosRecolector() {
        val userId = auth.currentUser?.uid ?: "DEV-USER-123"

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                combine(
                    getRutaActivaUseCase(userId),
                    getTotalIngresosUseCase(userId),
                    getTotalRutasCompletadasUseCase(userId)
                ) { rutaActiva, ingresos, completadas ->
                    HomeUIState(
                        isLoading = false,
                        hojaRutaActiva = rutaActiva,
                        totalIngresos = ingresos,
                        rutasCompletadas = completadas,
                        errorMessage = null
                    )
                }.collectLatest { newState ->
                    _state.value = newState
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}