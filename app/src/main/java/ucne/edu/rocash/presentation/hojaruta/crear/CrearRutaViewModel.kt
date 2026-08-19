package ucne.edu.rocash.presentation.hojaRuta.crear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
            }
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage
                                ?: "No se pudieron cargar las bancas"
                        ).conDerivadosResueltos()
                    }
                }
                .collect { (estaciones, comprometidas) ->
                    _state.update { actual ->
                        actual.copy(
                            isLoading = false,
                            estacionesDisponibles = estaciones,
                            estacionesComprometidas = comprometidas,

                            estacionesSeleccionadas =
                                actual.estacionesSeleccionadas - comprometidas,
                            errorMessage = null
                        ).conDerivadosResueltos()
                    }
                }
        }
    }

    private fun toggleEstacion(estacionId: Int) {
        _state.update { actual ->
            if (estacionId in actual.estacionesComprometidas) return@update actual

            val seleccionadas =
                if (estacionId in actual.estacionesSeleccionadas) {
                    actual.estacionesSeleccionadas - estacionId
                } else {
                    actual.estacionesSeleccionadas + estacionId
                }

            actual.copy(
                estacionesSeleccionadas = seleccionadas,
                errorMessage = null
            ).conDerivadosResueltos()
        }
    }

    private fun limpiarSeleccion() {
        _state.update {
            it.copy(estacionesSeleccionadas = emptySet()).conDerivadosResueltos()
        }
    }

    private fun generarRuta() {
        val actual = _state.value
        if (actual.isSaving) return

        _state.update {
            it.copy(isSaving = true, errorMessage = null).conDerivadosResueltos()
        }

        viewModelScope.launch {
            crearHojaRutaUseCase(
                recolectorId = sesion.recolectorIdOrNull(),
                estacionIds = actual.estacionesSeleccionadas.toList()
            )
                .onSuccess { rutaId ->
                    _state.update {
                        it.copy(isSaving = false, rutaCreadaId = rutaId)
                            .conDerivadosResueltos()
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message
                                ?: "No se pudo crear la hoja de ruta"
                        ).conDerivadosResueltos()
                    }
                }
        }
    }

    private fun CrearRutaUiState.conDerivadosResueltos(): CrearRutaUiState {
        val filas = estacionesDisponibles.map { estacion ->
            EstacionSeleccionableUi(
                estacion = estacion,
                seleccionada = estacion.estacionId in estacionesSeleccionadas,
                comprometida = estacion.estacionId in estacionesComprometidas
            )
        }

        return copy(
            estaciones = filas,
            hayEstaciones = filas.isNotEmpty(),
            cantidadSeleccionada = estacionesSeleccionadas.size,
            haySeleccion = estacionesSeleccionadas.isNotEmpty(),
            puedeGuardar = !isSaving && estacionesSeleccionadas.isNotEmpty()
        )
    }
}
