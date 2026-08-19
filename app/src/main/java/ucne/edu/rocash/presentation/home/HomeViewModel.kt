package ucne.edu.rocash.presentation.home

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
import ucne.edu.rocash.domain.hojaRuta.usecase.GetTotalIngresosUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.GetTotalRutasCompletadasUseCase
import ucne.edu.rocash.domain.hojaRuta.usecase.ObserveRutasAbiertasUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeRutasAbiertasUseCase: ObserveRutasAbiertasUseCase,
    private val getTotalIngresosUseCase: GetTotalIngresosUseCase,
    private val getTotalRutasCompletadasUseCase: GetTotalRutasCompletadasUseCase,
    private val sesion: SesionRecolector
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        onEvent(HomeUiEvent.CargarDatos)
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.CargarDatos -> cargarDatos()
            HomeUiEvent.ErrorMostrado -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun cargarDatos() {
        val recolectorId = sesion.recolectorIdOrNull()

        if (recolectorId == null) {
            _state.update {
                it.copy(
                    isLoading = false,
                    sinSesion = true,
                    mostrarAccionNuevaRuta = false
                )
            }
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
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage
                                ?: "No se pudieron cargar los datos"
                        )
                    }
                }
                .collect { (rutas, ingresos, completadas) ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            sinSesion = false,
                            rutasAbiertas = rutas,
                            hayRutasAbiertas = rutas.isNotEmpty(),
                            totalIngresos = ingresos,
                            rutasCompletadas = completadas,
                            mostrarAccionNuevaRuta = true,
                            errorMessage = null
                        )
                    }
                }
        }
    }
}
