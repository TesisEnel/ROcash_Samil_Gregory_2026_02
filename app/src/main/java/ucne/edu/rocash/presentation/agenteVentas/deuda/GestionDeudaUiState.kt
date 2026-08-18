package ucne.edu.rocash.presentation.agenteVentas.deuda

import ucne.edu.rocash.domain.abonoDeuda.model.AbonoDeuda

data class GestionDeudaUiState(
    val agenteId: Int = 0,
    val nombreAgente: String = "",
    val deudaActual: Double = 0.0,
    val tieneDeuda: Boolean = false,

    val montoAbono: String = "",
    val nota: String = "",
    val montoError: String? = null,

    val abonos: List<AbonoDeuda> = emptyList(),
    val hayAbonos: Boolean = false,
    val totalAbonado: Double = 0.0,

    val puedeAbonar: Boolean = false,
    val mostrarDialogoSaldar: Boolean = false,

    val isLoading: Boolean = true,
    val isProcesando: Boolean = false,
    val mensaje: String? = null,
    val errorMessage: String? = null
)
