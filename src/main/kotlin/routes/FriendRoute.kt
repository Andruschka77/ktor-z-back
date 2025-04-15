package com.example.routes

import com.example.data.model.response.BaseResponse
import com.example.data.model.tables.FriendTable
import com.example.data.model.tables.UserTable
import com.example.domain.usecase.FriendUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.FriendRoute(friendUseCase: FriendUseCase) {

    authenticate("jwt") {

        post("/api/v1/friends/requests") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val senderLogin = principal.payload.getClaim("login").asString()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Логин не найден")

            val body = try {
                call.receive<Map<String, String>>()
            } catch (e: Exception) {
                return@post call.respond(HttpStatusCode.BadRequest, "Неверный формат запроса")
            }

            val receiverLogin = body["receiver_login"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Логин получателя обязателен")

            try {
                val success = friendUseCase.sendRequest(senderLogin, receiverLogin)
                if (success) {
                    call.respond(BaseResponse(true, "Запрос отправлен"))
                } else {
                    call.respond(HttpStatusCode.Conflict, "Ошибка отправки запроса")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка сервера: ${e.message}")
            }
        }

        // Ответ на запрос
        post("api/v1/friends/response") {
            val principal = call.principal<JWTPrincipal>()!!
            val receiverLogin = principal.payload.getClaim("login").asString() // Получатель - текущий пользователь
            val body = call.receive<Map<String, String>>()
            val senderLogin = body["sender_login"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val accept = body["action"] == "accept"

            val success = friendUseCase.respondToRequest(senderLogin, receiverLogin, accept)
            call.respond(BaseResponse(success, if (success) "Успешно" else "Ошибка"))
        }

        // Удаление друга
        delete("/api/v1/friends/{friendLogin}") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Токен недействителен")

            val userLogin = principal.payload.getClaim("login").asString()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Логин не найден в токене")

            val friendLogin = call.parameters["friendLogin"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Логин друга не указан")

            val success = friendUseCase.removeFriend(userLogin, friendLogin)
            call.respond(BaseResponse(success, if (success) "Друг удалён" else "Ошибка"))
        }

        // Получение списка друзей
        get("/api/v1/friends") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Токен недействителен")

            val userLogin = principal.payload.getClaim("login").asString()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Логин не найден")

            try {
                val friends = friendUseCase.getFriends(userLogin)
                call.respond(friends)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка сервера")
            }
        }

    }
}
