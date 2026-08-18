package ucne.edu.rocash.presentation.hojaRuta.crear

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.auth.session.SesionRecolector
import ucne.edu.rocash.domain.estacion.usecase.ObserveEstacionesUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.CrearHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.ObserveEstacionesComprometidasUseCase
import ucne.edu.rocash.presentation.core.MviViewModel
import javax.inject.Inject

@HiltViewModel
class CrearRutaViewModel @Inject constructor(
    private val observeEstacionesUseCase: ObserveEstacionesUseCase,
    private val observeEstacionesComprometidasUseCase: ObserveEstacionesComprometidasUseCase,
    private val crearHojaRutaUseCase: CrearHojaRutaUseCase,
    private val sesion: SesionRecolector
) : MviViewModel<CrearRutaUiState, CrearRutaUiEvent>(CrearRutaUiState()) {

    init {
        onEvent(CrearRutaUiEvent.Load)
    }

    override fun onEvent(event: CrearRutaUiEvent) {
        when (event) {
            CrearRutaUiEvent.Load -> cargarEstaciones()

            is CrearRutaUiEvent.ToggleEstacion ->
                reduce { CrearRutaReducer.conEstacionAlternada(it, event.estacionId) }

            CrearRutaUiEvent.LimpiarSeleccion ->
                reduce(CrearRutaReducer::sinSeleccion)

            CrearRutaUiEvent.GenerarHojaRuta -> generarRuta()

            CrearRutaUiEvent.ErrorMostrado -> reduce(CrearRutaReducer::sinMensaje)
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
                    reduce { estado ->
                        CrearRutaReducer.conFalloDeCarga(
                            estado = estado,
                            mensaje = error.localizedMessage
                                ?: "No se pudieron cargar las bancas"
                        )
                    }
                }
                .collect { (estaciones, comprometidas) ->
                    reduce { estado ->
                        CrearRutaReducer.conEstaciones(
                            estado = estado,
                            disponibles = estaciones,
                            comprometidas = comprometidas
                        )
                    }
                }
        }
    }

    private fun generarRuta() {
        val actual = estadoActual
        if (actual.isSaving) return

        reduce(CrearRutaReducer::guardando)

        viewModelScope.launch {
            crearHojaRutaUseCase(
                recolectorId = sesion.recolectorIdOrNull(),
                estacionIds = actual.estacionesSeleccionadas.toList()
            )
                .onSuccess { rutaId ->
                    reduce { estado -> CrearRutaReducer.rutaCreada(estado, rutaId) }
                }
                .onFailure { error ->
                    reduce { estado ->
                        CrearRutaReducer.guardadoFallido(
                            estado = estado,
                            mensaje = error.message ?: "No se pudo crear la hoja de ruta"
                        )
                    }
                }
        }
    }
}
