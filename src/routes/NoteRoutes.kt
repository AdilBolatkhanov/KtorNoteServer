package com.ad.routes

import com.ad.data.getNotesForUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes() {
    route("/getNotes") {
        authenticate {
            get{
                val email = call.principal<UserIdPrincipal>()!!.name

                val notes = getNotesForUser(email)
                call.respond(HttpStatusCode.OK, notes)
            }
        }
    }
}