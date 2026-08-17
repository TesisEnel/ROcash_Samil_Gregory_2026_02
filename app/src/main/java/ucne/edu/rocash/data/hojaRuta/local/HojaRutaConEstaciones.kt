package ucne.edu.rocash.data.hojaRuta.local
import androidx.room.Embedded
import androidx.room.Relation

data class HojaRutaConEstaciones(
    @Embedded val ruta: HojaRutaEntity,

    @Relation(
        entity = HojaRutaEstacionEntity::class,
        parentColumn = "id",
        entityColumn = "hojaRutaId"
    )
    val estaciones: List<EstacionDeRutaEntity>
)