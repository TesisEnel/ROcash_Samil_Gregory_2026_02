package ucne.edu.rocash.presentation.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.auth.session.SesionRecolector
import ucne.edu.rocash.domain.hojaRuta.usecase.GetTotalIngresosUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.GetTotalRutasCompletadasUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.ObserveRutasAbiertasUseCase
import ucne.edu.rocash.presentation.core.MviViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeRutasAbiertasUseCase: ObserveRutasAbiertasUseCase,
    private val getTotalIngresosUseCase: GetTotalIngresosUseCase,
    private val getTotalRutasCompletadasUseCase: GetTotalRutasCompletadasUseCase,
    private val sesion: SesionRecolector
) : MviViewModel<HomeUiState, HomeUiEvent>(HomeUiState()) {

    init {
        onEvent(HomeUiEvent.CargarDatos)
    }

    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.CargarDatos -> cargarDatos()
            HomeUiEvent.ErrorMostrado -> reduce(HomeReducer::sinMensaje)
        }
    }

    private fun cargarDatos() {
        val recolectorId = sesion.recolectorIdOrNull()

        if (recolectorId == null) {
            reduce(HomeReducer::sinSesion)
            return
        }

        viewModelScope.launch {
            combine(
                observeRutasAbiertasUseCase(recolectorId),
                getTotalIngresosUseCase(recolectorId),
                getTotalRutasCompletadasUseCase(recolectorId)
            ) { rutas, ingresos, completadas ->
                Triple(rutas, ingresos, completadas)
            }
                .catch { error ->
                    reduce { estado ->
                        HomeReducer.conFalloDeCarga(
                            estado = estado,
                            mensaje = error.localizedMessage
                                ?: "No se pudieron cargar los datos"
                        )
                    }
                }
                .collect { (rutas, ingresos, completadas) ->
                    reduce { estado ->
                        HomeReducer.conDatos(
                            estado = estado,
                            rutas = rutas,
                            totalIngresos = ingresos,
                            rutasCompletadas = completadas
                        )
                    }
                }
        }
    }
}
