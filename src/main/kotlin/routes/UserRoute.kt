package com.example.routes

import com.example.authentification.hash
import com.example.data.model.UserModel
import com.example.data.model.requests.LoginRequest
import com.example.data.model.requests.RegisterRequest
import com.example.data.model.response.BaseResponse
import com.example.domain.usecase.UserUseCase
import com.example.utils.Constants
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Route.UserRoute(userUseCase: UserUseCase) {

    val hashFunction = { p: String -> hash(password = p) }

    post("api/v1/signup") {
        try {
            println(call)

            val registerRequest = call.receiveNullable<RegisterRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest, BaseResponse(false, Constants.Error.GENERAL))
                return@post
            }
        }
        catch (e: Exception) {
            println(e)
        }

//        try {
//            val user = UserModel(
//                id = 0,
//                email = registerRequest.email.trim().lowercase(),
//                login = registerRequest.login.trim().lowercase(),
//                password = hashFunction(registerRequest.password.trim()),
//                firstName = registerRequest.firstname.trim(),
//                lastName = registerRequest.lastname.trim()
//            )
//
//            userUseCase.createUser(user)
//            call.respond(HttpStatusCode.OK, BaseResponse(true, userUseCase.generateToken(userModel = user)))
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.Conflict, BaseResponse(false, e.message ?: Constants.Error.GENERAL))
//        }
    }

    post("api/v1/login") {
        val loginRequest = call.receiveNullable<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, BaseResponse(false, Constants.Error.GENERAL))
            return@post
        }

        try {
            val user = userUseCase.findUserByEmail(loginRequest.email.trim().lowercase())

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, BaseResponse(false, Constants.Error.WRONG_EMAIL))
            } else {
                if (user.password == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, userUseCase.generateToken(userModel = user))
                } else {
                    call.respond(HttpStatusCode.BadRequest, BaseResponse(false, Constants.Error.INCORRECT_PASSWORD))
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, BaseResponse(false, e.message ?: Constants.Error.GENERAL))
        }
    }
}