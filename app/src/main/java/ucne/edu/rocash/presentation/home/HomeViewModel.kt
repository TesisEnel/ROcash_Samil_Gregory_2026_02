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
import ucne.edu.rocash.domain.estacion.model.EstacionVentas
import ucne.edu.rocash.domain.model.EstadoRuta
import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.repository.RoCashRepository
import ucne.edu.rocash.domain.usecase.CrearEstacionUseCase
import ucne.edu.rocash.domain.usecase.CrearHojaRutaUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

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
        // Bypass para desarrollo: Asignamos un ID temporal si no hay usuario en Firebase
        val userId = auth.currentUser?.uid ?: "DEV-USER-123"

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
                    id = UUID.randomUUID().toString(),
                    hojaRutaId = nuevaRutaId,
                    agenteId = "AGE-001",
                    nombre = "Estación Los Prados",
                    direccion = "Av. Principal #123"
                )

                val estacion2 = EstacionVentas(
                    id = UUID.randomUUID().toString(),
                    hojaRutaId = nuevaRutaId,
                    agenteId = "AGE-002",
                    nombre = "Estación Centro",
                    direccion = "Calle Central #45"
                )

                crearEstacionUseCase(estacion1)
                crearEstacionUseCase(estacion2)

                // Recargamos la interfaz automáticamente después de insertar
                processIntent(HomeUIEvent.CargarDatos)

            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Error al insertar prueba: ${e.message}") }
            }
        }
    }
    private fun cargarDatosRecolector() {
        val userId = auth.currentUser?.uid ?: "DEV-USER-123"

        viewModelScope.launch {
            try {
                repository.obtenerHojaRutaActiva(userId)
                    .flatMapLatest { hoja ->
                        if (hoja != null) {
                            // Si hay hoja, buscamos sus estaciones y combinamos los datos
                            repository.obtenerEstacionesPorRuta(hoja.id).map { estaciones ->
                                hoja to estaciones // Retornamos un Pair<HojaRuta, List<Estacion>>
                            }
                        } else {
                            // Si no hay hoja, retornamos vacío
                            flowOf(null to emptyList())
                        }
                    }
                    .collectLatest { (hoja, listaEstaciones) ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                hojaRutaActiva = hoja,
                                estaciones = listaEstaciones,
                                errorMessage = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}