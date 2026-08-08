package ucne.edu.rocash.presentation.ruta

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
import ucne.edu.rocash.domain.estacion.usecase.GetEstacionesUseCase
import ucne.edu.rocash.domain.model.EstadoRuta
import ucne.edu.rocash.domain.model.HojaRuta
import ucne.edu.rocash.domain.repository.RoCashRepository
import ucne.edu.rocash.domain.usecase.AsignarRutaAEstacionUseCase
import ucne.edu.rocash.domain.usecase.CrearHojaRutaUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CrearRutaViewModel @Inject constructor(
    private val getEstacionesUseCase: GetEstacionesUseCase,
    private val crearHojaRutaUseCase: CrearHojaRutaUseCase,
    private val asignarRutaAEstacionUseCase: AsignarRutaAEstacionUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(CrearRutaUIState())
    val state: StateFlow<CrearRutaUIState> = _state.asStateFlow()

    init {
        processIntent(CrearRutaUIEvent.CargarEstaciones)
    }

    fun processIntent(intent: CrearRutaUIEvent) {
        when (intent) {
            is CrearRutaUIEvent.CargarEstaciones -> cargarEstaciones()
            is CrearRutaUIEvent.ToggleEstacionSeleccionada -> toggleSeleccion(intent.estacionId)
            is CrearRutaUIEvent.GenerarHojaRuta -> guardarRuta()
        }
    }

    private fun cargarEstaciones() {
        viewModelScope.launch {
            try {
                getEstacionesUseCase().collectLatest { lista ->
                    _state.update { it.copy(isLoading = false, estacionesDisponibles = lista) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    private fun toggleSeleccion(estacionId: String) {
        _state.update { currentState ->
            val seleccionadas = currentState.estacionesSeleccionadas.toMutableSet()
            if (seleccionadas.contains(estacionId)) {
                seleccionadas.remove(estacionId)
            } else {
                seleccionadas.add(estacionId)
            }
            currentState.copy(estacionesSeleccionadas = seleccionadas)
        }
    }

    private fun guardarRuta() {
        val currentState = _state.value
        if (currentState.estacionesSeleccionadas.isEmpty()) {
            _state.update { it.copy(errorMessage = "Debe seleccionar al menos una estación") }
            return
        }

        val recolectorId = auth.currentUser?.uid ?: "DEV-USER-123"

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val nuevaRutaId = UUID.randomUUID().toString()

                val nuevaRuta = HojaRuta(
                    id = nuevaRutaId,
                    recolectorId = recolectorId,
                    estado = EstadoRuta.EN_PROGRESO
                )

                crearHojaRutaUseCase(nuevaRuta)

                currentState.estacionesSeleccionadas.forEach { estacionId ->
                    asignarRutaAEstacionUseCase(estacionId, nuevaRutaId)
                }

                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}