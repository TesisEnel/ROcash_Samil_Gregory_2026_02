package ucne.edu.rocash.presentation.hojaRuta.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.auth.session.SesionRecolector
import ucne.edu.rocash.domain.hojaRuta.usecase.GetHistorialRutasUseCase
import javax.inject.Inject

@HiltViewModel
class HistorialRutasViewModel @Inject constructor(
    private val getHistorialRutasUseCase: GetHistorialRutasUseCase,
    private val sesion: SesionRecolector
) : ViewModel() {

    private val _state = MutableStateFlow(HistorialRutasUiState())
    val state: StateFlow<HistorialRutasUiState> = _state.asStateFlow()

    init {
        onEvent(HistorialRutasUiEvent.CargarHistorial)
    }

    fun onEvent(event: HistorialRutasUiEvent) {
        when (event) {
            HistorialRutasUiEvent.CargarHistorial -> cargarHistorial()
            HistorialRutasUiEvent.ErrorMostrado -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun cargarHistorial() {
        val recolectorId = sesion.recolectorIdOrNull()

        if (recolectorId == null) {
            _state.update { it.copy(isLoading = false, sinSesion = true) }
            return
        }

        viewModelScope.launch {
            getHistorialRutasUseCase(recolectorId)
                .catch { error ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = error.localizedMessage)
                    }
                }
                .collect { rutas ->
                    _state.update { it.copy(isLoading = false, rutas = rutas) }
                }
        }
    }
}