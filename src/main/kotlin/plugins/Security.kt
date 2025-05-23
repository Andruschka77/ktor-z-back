package com.example.plugins

import com.example.data.model.response.BaseResponse
import com.example.domain.usecase.UserUseCase
import com.example.utils.Constants
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity(userUseCase: UserUseCase) {
    authentication {
        jwt("jwt") {
            verifier(userUseCase.getJwtVerifier())
            realm = "Z server"

            validate { cred ->
                // 1) получаем email из токена
                val email = cred.payload.getClaim("email").asString()
                // 2) проверяем, что такой пользователь есть
                val user = userUseCase.findUserByEmail(email = email)
                // 3) если пользователь найден — возвращаем JWTPrincipal, иначе null
                if (user != null) JWTPrincipal(cred.payload) else null
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    BaseResponse(false, Constants.Error.GENERAL)
                )
            }
        }
    }
}
