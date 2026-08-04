package ucne.edu.rocash.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.model.EstacionVentas
import ucne.edu.rocash.domain.model.EstadoRuta
import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.repository.RoCashRepository
import ucne.edu.rocash.domain.usecase.CrearEstacionUseCase
import ucne.edu.rocash.domain.usecase.CrearHojaRutaUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RoCashRepository,
    private val crearHojaRutaUseCase: CrearHojaRutaUseCase,
    private val crearEstacionUseCase: CrearEstacionUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUIState())
    val state: StateFlow<HomeUIState> = _state.asStateFlow()

    init {
        processIntent(HomeUIEvent.CargarDatos)
    }

    fun processIntent(intent: HomeUIEvent) {
        when (intent) {
            is HomeUIEvent.CargarDatos -> cargarDatosRecolector()
            is HomeUIEvent.CrearRutaPrueba -> generarDatosDePrueba()
        }
    }

    private fun generarDatosDePrueba() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val nuevaRutaId = java.util.UUID.randomUUID().toString()
                val nuevaRuta = HojaRuta(
                    id = nuevaRutaId,
                    recolectorId = userId,
                    estado = EstadoRuta.EN_PROGRESO
                )

                crearHojaRutaUseCase(nuevaRuta)

                val estacion1 = EstacionVentas(
                    id = java.util.UUID.randomUUID().toString(),
                    hojaRutaId = nuevaRutaId,
                    agenteId = "AGE-001",
                    nombre = "Estación Los Prados",
                    direccion = "Av. Principal #123"
                )

                val estacion2 = EstacionVentas(
                    id = java.util.UUID.randomUUID().toString(),
                    hojaRutaId = nuevaRutaId,
                    agenteId = "AGE-002",
                    nombre = "Estación Centro",
                    direccion = "Calle Central #45"
                )

                crearEstacionUseCase(estacion1)
                crearEstacionUseCase(estacion2)


            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Error al insertar prueba: ${e.message}") }
            }
        }
    }

    private fun cargarDatosRecolector() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _state.update { it.copy(isLoading = false, errorMessage = "Error: Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            try {
                repository.obtenerHojaRutaActiva(userId).collectLatest { hoja ->
                    if (hoja != null) {
                        repository.obtenerEstacionesPorRuta(hoja.id).collectLatest { listaEstaciones ->
                            _state.update {
                                it.copy(isLoading = false, hojaRutaActiva = hoja, estaciones = listaEstaciones)
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(isLoading = false, hojaRutaActiva = null, estaciones = emptyList())
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}