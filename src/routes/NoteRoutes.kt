package com.ad.routes

import com.ad.data.collections.Note
import com.ad.data.deleteNoteForUser
import com.ad.data.getNotesForUser
import com.ad.data.requests.DeleteNoteRequest
import com.ad.data.saveNote
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes() {
    route("/getNotes") {
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name

                val notes = getNotesForUser(email)
                call.respond(HttpStatusCode.OK, notes)
                return@get
            }
        }
    }

    route("/deleteNote") {
        authenticate {
            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (deleteNoteForUser(email, request.id))
                    call.respond(HttpStatusCode.OK)
                else
                    call.respond(HttpStatusCode.Conflict)
            }
        }
    }

    route("/addNote") {
        authenticate {
            post {
                val note = try {
                    call.receive<Note>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (saveNote(note))
                    call.respond(HttpStatusCode.OK)
                else
                    call.respond(HttpStatusCode.Conflict)
            }
        }
    }
}