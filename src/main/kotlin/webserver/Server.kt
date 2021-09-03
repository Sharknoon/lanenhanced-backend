package webserver

import com.google.gson.Gson
import database.getGame
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import models.Game

private val gson = Gson()

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            route("/api") {
                route("/games") {
                    get {
                        getAllGames()
                    }
                    post("/new") {
                        newGame()
                    }
                    get("/{id}") {
                        getGame()
                    }
                    patch("/{id}") {
                        editGame()
                    }
                    delete("/{id}") {
                        deleteGame()
                    }
                }
            }
        }
    }.start(wait = true)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getAllGames() {
    call.respondText(gson.toJson(database.getAllGames()))
}

private suspend fun PipelineContext<Unit, ApplicationCall>.newGame() {
    val magnetURI = call.request.queryParameters["magnetURI"]
        ?: return call.respond(HttpStatusCode.BadRequest, "Missing Query Parameter 'magnetURI'")
    val name = call.request.queryParameters["name"]
        ?: return call.respond(HttpStatusCode.BadRequest, "Missing Query Parameter 'name'")
    val description = call.request.queryParameters["description"]
        ?: return call.respond(HttpStatusCode.BadRequest, "Missing Query Parameter 'description'")
    val game = Game(magnetURI, name, description)
    val affected = database.newGame(game)
    if (affected == 1) {
        call.respond(gson.toJson(game))
    } else {
        call.respond(HttpStatusCode.InternalServerError)
    }
    call.respond(if (affected == 1) HttpStatusCode.OK else HttpStatusCode.InternalServerError)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getGame() {
    val id = call.request.queryParameters["id"]?.toLongOrNull()
        ?: return call.respond(HttpStatusCode.BadRequest, "Missing Query Parameter 'id'")
    val game = getGame(id)
        ?: return call.respond(HttpStatusCode.NotFound, "Requested Object not found")
    call.respondText(gson.toJson(game))
}

private suspend fun PipelineContext<Unit, ApplicationCall>.editGame() {
    val magnetURI = call.request.queryParameters["magnetURI"]
        ?: return call.respond(HttpStatusCode.BadRequest, "Missing Query Parameter 'magnetURI'")
    val name = call.request.queryParameters["name"]
        ?: return call.respond(HttpStatusCode.BadRequest, "Missing Query Parameter 'name'")
    val description = call.request.queryParameters["description"]
        ?: return call.respond(HttpStatusCode.BadRequest, "Missing Query Parameter 'description'")

    val affected = database.newGame(Game(magnetURI, name, description))
    call.respond(if (affected == 1) HttpStatusCode.OK else HttpStatusCode.InternalServerError)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.deleteGame() {
    val id = call.request.queryParameters["id"]?.toLongOrNull()
        ?: return call.respond(HttpStatusCode.BadRequest, "Missing Query Parameter 'id'")
    val affected = database.deleteGame(id)
    call.respond(if (affected == 1) HttpStatusCode.OK else HttpStatusCode.NotFound)
}