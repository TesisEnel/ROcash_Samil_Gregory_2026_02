package ucne.edu.rocash.presentation.hojaRuta.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.hojaRuta.usecase.CerrarHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.ObserveHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.OmitirEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ObservarResumenDeRutaUseCase
import javax.inject.Inject

@HiltViewModel
class DetalleRutaViewModel @Inject constructor(
    private val observeHojaRutaUseCase: ObserveHojaRutaUseCase,
    private val observarResumenDeRutaUseCase: ObservarResumenDeRutaUseCase,
    private val cerrarHojaRutaUseCase: CerrarHojaRutaUseCase,
    private val omitirEstacionUseCase: OmitirEstacionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetalleRutaUiState())
    val state: StateFlow<DetalleRutaUiState> = _state.asStateFlow()

    private var observacion: Job? = null

    fun onEvent(event: DetalleRutaUiEvent) {
        when (event) {
            is DetalleRutaUiEvent.Load -> cargar(event.rutaId)
            DetalleRutaUiEvent.PedirConfirmacionCierre ->
                _state.update { it.copy(mostrarDialogoCierre = true) }
            DetalleRutaUiEvent.CancelarCierre ->
                _state.update { it.copy(mostrarDialogoCierre = false) }
            DetalleRutaUiEvent.ConfirmarCierre -> cerrarRuta()
            is DetalleRutaUiEvent.OmitirEstacion -> omitirEstacion(event.estacionId)
            DetalleRutaUiEvent.ErrorMostrado -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun cargar(rutaId: Int) {
        if (observacion != null && _state.value.rutaId == rutaId) return

        observacion?.cancel()
        _state.update { it.copy(rutaId = rutaId, isLoading = true) }

        observacion = viewModelScope.launch {
            combine(
                observeHojaRutaUseCase(rutaId),
                observarResumenDeRutaUseCase(rutaId)
            ) { ruta, resumen -> ruta to resumen }
                .catch { error ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = error.localizedMessage)
                    }
                }
                .collect { (ruta, resumen) ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            ruta = ruta,
                            resumen = resumen,
                            noEncontrada = ruta == null
                        )
                    }
                }
        }
    }

    private fun cerrarRuta() {
        val rutaId = _state.value.rutaId
        if (rutaId == 0) return

        _state.update { it.copy(isCerrando = true, mostrarDialogoCierre = false) }

        viewModelScope.launch {
            cerrarHojaRutaUseCase(rutaId)
                .onSuccess {
                    _state.update { it.copy(isCerrando = false, rutaCerrada = true) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isCerrando = false,
                            errorMessage = error.message ?: "No se pudo cerrar la ruta"
                        )
                    }
                }
        }
    }

    private fun omitirEstacion(estacionId: Int) {
        val rutaId = _state.value.rutaId

        viewModelScope.launch {
            omitirEstacionUseCase(rutaId, estacionId).onFailure { error ->
                _state.update {
                    it.copy(errorMessage = error.message ?: "No se pudo omitir la estación")
                }
            }
        }
    }
}