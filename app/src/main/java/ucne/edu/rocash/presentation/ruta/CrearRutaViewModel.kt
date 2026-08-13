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
import ucne.edu.rocash.domain.estacion.usecase.AsignarRutaAEstacionUseCase
import ucne.edu.rocash.domain.estacion.usecase.ObserveEstacionesUseCase
import ucne.edu.rocash.domain.hojaRuta.model.EstadoRuta
import ucne.edu.rocash.domain.hojaRuta.model.HojaRuta
import ucne.edu.rocash.domain.hojaRuta.usecase.CrearHojaRutaUseCase
import javax.inject.Inject

@HiltViewModel
class CrearRutaViewModel @Inject constructor(
    private val observeEstacionesUseCase: ObserveEstacionesUseCase,
    private val crearHojaRutaUseCase: CrearHojaRutaUseCase,
    private val asignarRutaAEstacionUseCase: AsignarRutaAEstacionUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(CrearRutaUIState(isLoading = true))
    val state: StateFlow<CrearRutaUIState> = _state.asStateFlow()

    init {
        onEvent(CrearRutaUIEvent.Load)
    }

    fun onEvent(event: CrearRutaUIEvent) {
        when (event) {
            CrearRutaUIEvent.Load -> cargarEstaciones()
            is CrearRutaUIEvent.ToggleEstacionSeleccionada -> toggleEstacion(event.id)
            CrearRutaUIEvent.GenerarHojaRuta -> generarRuta()
        }
    }

    private fun cargarEstaciones() {
        viewModelScope.launch {
            observeEstacionesUseCase().collectLatest { lista ->
                _state.update {
                    it.copy(
                        estacionesDisponibles = lista,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun toggleEstacion(id: Int) {
        _state.update { currentState ->
            val seleccionadas = currentState.estacionesSeleccionadas.toMutableSet()
            if (seleccionadas.contains(id)) {
                seleccionadas.remove(id)
            } else {
                seleccionadas.add(id)
            }
            currentState.copy(estacionesSeleccionadas = seleccionadas)
        }
    }

    private fun generarRuta() {
        val currentState = state.value
        val recolectorId = auth.currentUser?.uid

        if (recolectorId == null) {
            _state.update { it.copy(errorMessage = "Error: Usuario no autenticado") }
            return
        }

        if (currentState.estacionesSeleccionadas.isEmpty()) {
            _state.update { it.copy(errorMessage = "Debe seleccionar al menos una estación") }
            return
        }

        _state.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val nuevaHojaRuta = HojaRuta(
                    recolectorId = recolectorId,
                    fechaCreacion = System.currentTimeMillis(),
                    estado = EstadoRuta.PENDIENTE,
                    totalVentaBruta = 0.0,
                    totalComisionClientes = 0.0,
                    totalRecaudado = 0.0,
                    totalDeudas = 0.0
                )

                val rutaId = crearHojaRutaUseCase(nuevaHojaRuta)

                currentState.estacionesSeleccionadas.forEach { estacionId ->
                    asignarRutaAEstacionUseCase(estacionId, rutaId)
                }

                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}