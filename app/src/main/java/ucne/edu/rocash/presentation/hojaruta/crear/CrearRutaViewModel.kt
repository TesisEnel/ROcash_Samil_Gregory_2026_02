package ucne.edu.rocash.presentation.hojaRuta.crear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.auth.session.SesionRecolector
import ucne.edu.rocash.domain.estacion.usecase.ObserveEstacionesUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.CrearHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.ObserveEstacionesComprometidasUseCase
import javax.inject.Inject

@HiltViewModel
class CrearRutaViewModel @Inject constructor(
    private val observeEstacionesUseCase: ObserveEstacionesUseCase,
    private val observeEstacionesComprometidasUseCase: ObserveEstacionesComprometidasUseCase,
    private val crearHojaRutaUseCase: CrearHojaRutaUseCase,
    private val sesion: SesionRecolector
) : ViewModel() {

    private val _state = MutableStateFlow(CrearRutaUiState())
    val state: StateFlow<CrearRutaUiState> = _state.asStateFlow()

    init {
        onEvent(CrearRutaUiEvent.Load)
    }

    fun onEvent(event: CrearRutaUiEvent) {
        when (event) {
            CrearRutaUiEvent.Load -> cargarEstaciones()
            is CrearRutaUiEvent.ToggleEstacion -> toggleEstacion(event.estacionId)
            CrearRutaUiEvent.LimpiarSeleccion -> limpiarSeleccion()
            CrearRutaUiEvent.GenerarHojaRuta -> generarRuta()
            CrearRutaUiEvent.ErrorMostrado -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun cargarEstaciones() {
        viewModelScope.launch {
            combine(
                observeEstacionesUseCase(),
                observeEstacionesComprometidasUseCase()
            ) { estaciones, comprometidas ->
                estaciones to comprometidas
            }.collect { (estaciones, comprometidas) ->
                _state.update { current ->
                    current.copy(
                        estacionesDisponibles = estaciones,
                        estacionesComprometidas = comprometidas,
                        // Si una banca fue tomada por otra ruta mientras esta
                        // pantalla estaba abierta, se retira de la seleccion.
                        estacionesSeleccionadas = current.estacionesSeleccionadas - comprometidas,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun toggleEstacion(estacionId: Int) {
        _state.update { current ->
            if (current.estaComprometida(estacionId)) return@update current

            val seleccionadas = current.estacionesSeleccionadas.toMutableSet()
            if (!seleccionadas.add(estacionId)) seleccionadas.remove(estacionId)

            current.copy(estacionesSeleccionadas = seleccionadas, errorMessage = null)
        }
    }

    private fun limpiarSeleccion() {
        _state.update { it.copy(estacionesSeleccionadas = emptySet()) }
    }

    private fun generarRuta() {
        val current = _state.value
        if (current.isSaving) return

        _state.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            val resultado = crearHojaRutaUseCase(
                recolectorId = sesion.recolectorIdOrNull(),
                estacionIds = current.estacionesSeleccionadas.toList()
            )

            resultado
                .onSuccess { rutaId ->
                    _state.update { it.copy(isSaving = false, rutaCreadaId = rutaId) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "No se pudo crear la hoja de ruta"
                        )
                    }
                }
        }
    }
}