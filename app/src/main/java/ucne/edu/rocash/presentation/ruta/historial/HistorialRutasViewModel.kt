package ucne.edu.rocash.presentation.ruta.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.hojaRuta.usecase.GetHistorialRutasUseCase
import javax.inject.Inject

@HiltViewModel
class HistorialRutasViewModel @Inject constructor(
    private val getHistorialRutasUseCase: GetHistorialRutasUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(HistorialRutasUiState())
    val state: StateFlow<HistorialRutasUiState> = _state.asStateFlow()

    init {
        processIntent(HistorialRutasUiEvent.CargarHistorial)
    }

    fun processIntent(intent: HistorialRutasUiEvent) {
        when (intent) {
            is HistorialRutasUiEvent.CargarHistorial -> cargarHistorial()
        }
    }

    private fun cargarHistorial() {
        val userId = auth.currentUser?.uid ?: "DEV-USER-123"

        viewModelScope.launch {
            try {
                getHistorialRutasUseCase(userId).collectLatest { listaRutas ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            rutas = listaRutas,
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