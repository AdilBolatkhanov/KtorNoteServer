package com.ad.routes

import com.ad.data.*
import com.ad.data.collections.Note
import com.ad.data.requests.AddOwnerRequest
import com.ad.data.requests.DeleteNoteRequest
import com.ad.data.responses.SimpleResponse
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

    route("/addOwnerToNote") {
        authenticate {
            post {
                val request = try {
                    call.receive<AddOwnerRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (!checkIfUserExists(request.owner)) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(false, "No user with this email exists")
                    )
                }
                if (isOwnerOfNote(request.noteId, request.owner)) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(
                            false,
                            "This user is already an owner of this course"
                        )
                    )
                    return@post
                }
                if (addOwnerToNote(request.noteId, request.owner)) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(true, "${request.owner} can now see this note")
                    )
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
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