package ucne.edu.rocash.presentation.common

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val monedaDo: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "DO"))

fun Double.aMoneda(): String = monedaDo.format(this)

private const val PATRON_FECHA = "dd/MM/yyyy hh:mm a"

fun Long.aFechaLegible(): String =
    SimpleDateFormat(PATRON_FECHA, Locale("es", "DO")).format(Date(this))