package database

import models.Game
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.filters.ObjectFilters.eq
import java.nio.file.Files
import java.nio.file.Paths

private val db = nitrite {
    file = Paths.get("data")
        .toAbsolutePath()
        .also { Files.createDirectories(it) }
        .resolve("database.db")
        .toFile()
}

private val gameRepository = db.getRepository<Game> { }

fun getAllGames(): List<Game> = gameRepository.find().toList()
fun newGame(game: Game): Int = gameRepository.insert(game).affectedCount
fun getGame(id: Long): Game? = gameRepository.getById(NitriteId.createId(id))
fun editGame(game: Game):Int = gameRepository.update(game).affectedCount
fun deleteGame(id: Long):Int = gameRepository.remove(eq("id", id)).affectedCount
