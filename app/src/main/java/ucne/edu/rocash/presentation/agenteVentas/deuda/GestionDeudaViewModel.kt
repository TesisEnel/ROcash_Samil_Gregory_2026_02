package ucne.edu.rocash.presentation.agenteVentas.deuda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.rocash.domain.abonoDeuda.usecase.ObservarAbonosDeAgenteUseCase
import ucne.edu.rocash.domain.abonoDeuda.usecase.ObservarTotalAbonadoUseCase
import ucne.edu.rocash.domain.abonoDeuda.usecase.RegistrarAbonoUseCase
import ucne.edu.rocash.domain.abonoDeuda.usecase.SaldarDeudaUseCase
import ucne.edu.rocash.domain.agenteVentas.usecase.ObservarAgenteUseCase
import javax.inject.Inject

@HiltViewModel
class GestionDeudaViewModel @Inject constructor(
    private val observarAgenteUseCase: ObservarAgenteUseCase,
    private val observarAbonosDeAgenteUseCase: ObservarAbonosDeAgenteUseCase,
    private val observarTotalAbonadoUseCase: ObservarTotalAbonadoUseCase,
    private val registrarAbonoUseCase: RegistrarAbonoUseCase,
    private val saldarDeudaUseCase: SaldarDeudaUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(GestionDeudaUiState())
    val state: StateFlow<GestionDeudaUiState> = _state.asStateFlow()

    private var observacion: Job? = null

    fun onEvent(event: GestionDeudaUiEvent) {
        when (event) {
            is GestionDeudaUiEvent.Load -> cargar(event.agenteId)

            is GestionDeudaUiEvent.MontoChanged -> _state.update {
                it.copy(montoAbono = event.value, montoError = null)
                    .conDerivadosResueltos()
            }

            is GestionDeudaUiEvent.NotaChanged -> _state.update { it.copy(nota = event.value) }

            GestionDeudaUiEvent.Abonar -> abonar()

            GestionDeudaUiEvent.PedirConfirmacionSaldar ->
                _state.update { it.copy(mostrarDialogoSaldar = true) }

            GestionDeudaUiEvent.CancelarSaldar ->
                _state.update { it.copy(mostrarDialogoSaldar = false) }

            GestionDeudaUiEvent.Saldar -> saldar()

            GestionDeudaUiEvent.MensajeMostrado -> _state.update { it.copy(mensaje = null) }
            GestionDeudaUiEvent.ErrorMostrado -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun cargar(agenteId: Int) {
        if (observacion != null && _state.value.agenteId == agenteId) return

        observacion?.cancel()
        _state.update { it.copy(agenteId = agenteId, isLoading = true) }

        observacion = viewModelScope.launch {
            combine(
                observarAgenteUseCase(agenteId),
                observarAbonosDeAgenteUseCase(agenteId),
                observarTotalAbonadoUseCase(agenteId)
            ) { agente, abonos, totalAbonado ->
                Triple(agente, abonos, totalAbonado)
            }
                .collect { (agente, abonos, totalAbonado) ->
                    _state.update { actual ->
                        actual.copy(
                            isLoading = false,
                            nombreAgente = agente?.nombre ?: actual.nombreAgente,
                            deudaActual = agente?.deudaAcumulada ?: 0.0,
                            abonos = abonos,
                            totalAbonado = totalAbonado
                        ).conDerivadosResueltos()
                    }
                }
        }
    }

    private fun abonar() {
        val actual = _state.value
        if (actual.isProcesando) return

        _state.update { it.copy(isProcesando = true).conDerivadosResueltos() }

        viewModelScope.launch {
            registrarAbonoUseCase(
                agenteId = actual.agenteId,
                montoTexto = actual.montoAbono,
                nota = actual.nota
            )
                .onSuccess { abono ->
                    _state.update {
                        it.copy(
                            isProcesando = false,
                            montoAbono = "",
                            nota = "",
                            montoError = null,
                            mensaje = if (abono.deudaDespues <= 0.0) {
                                "Abono registrado. La deuda quedó saldada."
                            } else {
                                "Abono registrado."
                            }
                        ).conDerivadosResueltos()
                    }
                }
                .onFailure { error ->
                    val esValidacion = error is IllegalArgumentException
                    _state.update {
                        it.copy(
                            isProcesando = false,
                            montoError = if (esValidacion) error.message else it.montoError,
                            errorMessage = if (esValidacion) {
                                it.errorMessage
                            } else {
                                error.message ?: "No se pudo registrar el abono"
                            }
                        ).conDerivadosResueltos()
                    }
                }
        }
    }

    private fun saldar() {
        val actual = _state.value
        if (actual.isProcesando) return

        _state.update {
            it.copy(isProcesando = true, mostrarDialogoSaldar = false).conDerivadosResueltos()
        }

        viewModelScope.launch {
            saldarDeudaUseCase(agenteId = actual.agenteId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isProcesando = false,
                            montoAbono = "",
                            nota = "",
                            montoError = null,
                            mensaje = "Deuda saldada."
                        ).conDerivadosResueltos()
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isProcesando = false,
                            errorMessage = error.message ?: "No se pudo saldar la deuda"
                        ).conDerivadosResueltos()
                    }
                }
        }
    }

    private fun GestionDeudaUiState.conDerivadosResueltos(): GestionDeudaUiState = copy(
        tieneDeuda = deudaActual > 0.0,
        hayAbonos = abonos.isNotEmpty(),
        puedeAbonar = !isProcesando &&
                !isLoading &&
                deudaActual > 0.0 &&
                montoAbono.isNotBlank()
    )
}
