package ucne.edu.rocash.domain.model
enum class RolUsuario { ADMIN, RECOLECTOR }

data class Usuario(
    val id: String = "0",
    val nombre: String,
    val cedula: String,
    val rol: RolUsuario
)