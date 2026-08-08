package ucne.edu.rocash.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.repository.RoCashRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RoCashRepository,
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
                repository.obtenerHojaRutaActiva(userId)
                    .flatMapLatest { hoja ->
                        if (hoja != null) {
                            repository.obtenerEstacionesPorRuta(hoja.id).map { estaciones ->
                                hoja to estaciones
                            }
                        } else {
                            flowOf(null to emptyList())
                        }
                    }
                    .collectLatest { (hoja, listaEstaciones) ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                hojaRutaActiva = hoja,
                                estaciones = listaEstaciones,
                                errorMessage = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}