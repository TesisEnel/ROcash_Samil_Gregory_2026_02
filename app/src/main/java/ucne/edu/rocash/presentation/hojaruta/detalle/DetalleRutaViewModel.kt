package ucne.edu.rocash.presentation.hojaRuta.detalle

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.hojaRuta.usecase.CerrarHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.ObserveHojaRutaUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.OmitirEstacionUseCase
import ucne.edu.rocash.domain.registroRecoleccion.usecase.ObservarResumenDeRutaUseCase
import ucne.edu.rocash.presentation.core.MviViewModel
import javax.inject.Inject

@HiltViewModel
class DetalleRutaViewModel @Inject constructor(
    private val observeHojaRutaUseCase: ObserveHojaRutaUseCase,
    private val observarResumenDeRutaUseCase: ObservarResumenDeRutaUseCase,
    private val cerrarHojaRutaUseCase: CerrarHojaRutaUseCase,
    private val omitirEstacionUseCase: OmitirEstacionUseCase
) : MviViewModel<DetalleRutaUiState, DetalleRutaUiEvent>(DetalleRutaUiState()) {

    private var observacion: Job? = null

    override fun onEvent(event: DetalleRutaUiEvent) {
        when (event) {
            is DetalleRutaUiEvent.Load -> cargar(event.rutaId)
            DetalleRutaUiEvent.PedirConfirmacionCierre ->
                reduce(DetalleRutaReducer::pidiendoConfirmacion)
            DetalleRutaUiEvent.CancelarCierre ->
                reduce(DetalleRutaReducer::cancelandoConfirmacion)
            DetalleRutaUiEvent.ConfirmarCierre -> cerrarRuta()
            is DetalleRutaUiEvent.OmitirEstacion -> omitirEstacion(event.estacionId)
            DetalleRutaUiEvent.ErrorMostrado -> reduce(DetalleRutaReducer::sinMensaje)
        }
    }

    private fun cargar(rutaId: Int) {
        if (observacion != null && estadoActual.rutaId == rutaId) return

        observacion?.cancel()
        reduce { DetalleRutaReducer.cargando(it, rutaId) }

        observacion = viewModelScope.launch {
            combine(
                observeHojaRutaUseCase(rutaId),
                observarResumenDeRutaUseCase(rutaId)
            ) { ruta, resumen -> ruta to resumen }
                .catch { error ->
                    reduce { estado ->
                        DetalleRutaReducer.conFalloDeCarga(
                            estado = estado,
                            mensaje = error.localizedMessage ?: "No se pudo cargar la ruta"
                        )
                    }
                }
                .collect { (ruta, resumen) ->
                    reduce { DetalleRutaReducer.conRuta(it, ruta, resumen) }
                }
        }
    }

    private fun cerrarRuta() {
        val rutaId = estadoActual.rutaId
        if (rutaId == 0 || estadoActual.isCerrando) return

        reduce(DetalleRutaReducer::iniciandoCierre)

        viewModelScope.launch {
            cerrarHojaRutaUseCase(rutaId)
                .onSuccess {
                    reduce(DetalleRutaReducer::cierreExitoso)
                }
                .onFailure { error ->
                    reduce { estado ->
                        DetalleRutaReducer.cierreFallido(
                            estado = estado,
                            mensaje = error.message ?: "No se pudo cerrar la ruta"
                        )
                    }
                }
        }
    }

    private fun omitirEstacion(estacionId: Int) {
        val rutaId = estadoActual.rutaId

        viewModelScope.launch {
            omitirEstacionUseCase(rutaId, estacionId).onFailure { error ->
                reduce { estado ->
                    DetalleRutaReducer.conMensaje(
                        estado = estado,
                        mensaje = error.message ?: "No se pudo omitir la estación"
                    )
                }
            }
        }
    }
}
