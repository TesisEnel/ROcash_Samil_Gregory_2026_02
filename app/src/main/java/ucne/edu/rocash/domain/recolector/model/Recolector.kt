package ucne.edu.rocash.domain.recolector.model

data class Recolector(
    val id: String,
    val nombre: String,
    val telefono: String,
    val cedula: String,
    val estado: Boolean = true

)
//Semana dos: Agregar propiedad cedula al recolector