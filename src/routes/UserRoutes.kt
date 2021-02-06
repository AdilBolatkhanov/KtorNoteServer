package com.ad.routes


import com.ad.data.checkIfUserExists
import com.ad.data.checkPasswordForEmail
import com.ad.data.collections.User
import com.ad.data.registerUser
import com.ad.data.requests.AccountRequest
import com.ad.data.responses.SimpleResponse
import com.ad.security.getHashWithSalt
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute() {
    route("/register") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExists = checkIfUserExists(request.email)
            if (!userExists) {
                if (registerUser(User(request.email, getHashWithSalt(request.password))))
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Successfully created account!"))
                else
                    call.respond(HttpStatusCode.OK, SimpleResponse(false, "An unknown error occured!"))
            } else {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "A user with such email already exists!"))
            }
        }
    }
}

fun Route.loginRoute() {
    route("/login") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val isPasswordCorrect = checkPasswordForEmail(request.email, request.password)
            if (isPasswordCorrect)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "You are now logged in!"))
            else
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "The password or email is incorrect"))
        }
    }
}