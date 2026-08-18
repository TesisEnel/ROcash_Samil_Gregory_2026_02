package ucne.edu.rocash.presentation.hojaRuta.historial

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.auth.session.SesionRecolector
import ucne.edu.rocash.domain.hojaRuta.usecase.GetHistorialRutasUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.GetTotalIngresosUseCase
import ucne.edu.rocash.presentation.core.MviViewModel
import javax.inject.Inject

@HiltViewModel
class HistorialRutasViewModel @Inject constructor(
    private val getHistorialRutasUseCase: GetHistorialRutasUseCase,
    private val getTotalIngresosUseCase: GetTotalIngresosUseCase,
    private val sesion: SesionRecolector
) : MviViewModel<HistorialRutasUiState, HistorialRutasUiEvent>(HistorialRutasUiState()) {

    init {
        onEvent(HistorialRutasUiEvent.CargarHistorial)
    }

    override fun onEvent(event: HistorialRutasUiEvent) {
        when (event) {
            HistorialRutasUiEvent.CargarHistorial -> cargarHistorial()
            HistorialRutasUiEvent.ErrorMostrado -> reduce(HistorialRutasReducer::sinMensaje)
        }
    }

    private fun cargarHistorial() {
        val recolectorId = sesion.recolectorIdOrNull()

        if (recolectorId == null) {
            reduce(HistorialRutasReducer::sinSesion)
            return
        }

        viewModelScope.launch {
            // El total ya no se suma en la UI: lo agrega SQLite y viaja como Flow.
            combine(
                getHistorialRutasUseCase(recolectorId),
                getTotalIngresosUseCase(recolectorId)
            ) { rutas, total -> rutas to total }
                .catch { error ->
                    reduce { estado ->
                        HistorialRutasReducer.conFalloDeCarga(
                            estado = estado,
                            mensaje = error.localizedMessage
                                ?: "No se pudo cargar el historial"
                        )
                    }
                }
                .collect { (rutas, total) ->
                    reduce { estado ->
                        HistorialRutasReducer.conHistorial(
                            estado = estado,
                            rutas = rutas,
                            totalRecaudadoHistorico = total
                        )
                    }
                }
        }
    }
}
