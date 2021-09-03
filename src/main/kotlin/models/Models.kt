package models

import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.Id

data class Game(
    val magnetURI: String,
    val name: String,
    val description: String,
    @Id val id: Long = NitriteId.newId().idValue,
)