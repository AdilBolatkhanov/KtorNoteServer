package com.ad.routes


import com.ad.data.checkIfUserExists
import com.ad.data.collections.User
import com.ad.data.registerUser
import com.ad.data.requests.AccountRequest
import com.ad.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute(){
    route("/register"){
        post {
            val request = try{
                call.receive<AccountRequest>()
            }catch (e: ContentTransformationException){
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExists = checkIfUserExists(request.email)
            if (!userExists){
                if (registerUser(User(request.email, request.password))){
                    call.respond(HttpStatusCode.OK,SimpleResponse(true, "Successfully created account!"))
                }else{
                    call.respond(HttpStatusCode.OK ,SimpleResponse(false, "An unknown error occured!"))
                }
            }else{
                call.respond(HttpStatusCode.OK ,SimpleResponse(false, "A user with such email already exists!"))
            }
        }
    }
}