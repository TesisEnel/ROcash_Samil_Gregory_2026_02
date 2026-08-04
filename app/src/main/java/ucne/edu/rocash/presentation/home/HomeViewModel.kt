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
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _state.update { it.copy(isLoading = false, errorMessage = "Error: Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            try {
                repository.obtenerHojaRutaActiva(userId).collectLatest { hoja ->
                    if (hoja != null) {
                        repository.obtenerEstacionesPorRuta(hoja.id).collectLatest { listaEstaciones ->
                            _state.update {
                                it.copy(isLoading = false, hojaRutaActiva = hoja, estaciones = listaEstaciones)
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(isLoading = false, hojaRutaActiva = null, estaciones = emptyList())
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}