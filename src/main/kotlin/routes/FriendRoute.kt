package com.example.routes

import com.example.data.model.response.BaseResponse
import com.example.domain.usecase.FriendUseCase
import com.example.domain.usecase.UserUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Route.FriendRoute(userUseCase: UserUseCase, friendUseCase: FriendUseCase) {

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

            val email = body["email"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Email обязателен")

            val receiver = userUseCase.findUserByEmail(email)
                ?: return@post call.respond(HttpStatusCode.NotFound, BaseResponse(false, "Пользователь не найден"))

            try {
                val success = friendUseCase.sendRequest(senderLogin, receiver.login)
                if (success) {
                    call.respond(BaseResponse(true, "Запрос отправлен по email"))
                } else {
                    call.respond(HttpStatusCode.Conflict, "Ошибка отправки запроса")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка сервера: ${e.message}")
            }
        }

        // Ответ на запрос дружбы
        post("/api/v1/friends/response") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val receiverLogin = principal.payload.getClaim("login").asString()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Логин не найден")

            val body = try {
                call.receive<Map<String, String>>()
            } catch (e: Exception) {
                return@post call.respond(HttpStatusCode.BadRequest, "Неверный формат запроса")
            }

            val senderLogin = body["sender_login"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Логин отправителя не указан")
            val action = body["action"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Действие не указано")

            try {
                val success = friendUseCase.respondToRequest(
                    senderLogin = senderLogin,
                    receiverLogin = receiverLogin,
                    accept = action == "accept"
                )
                call.respond(BaseResponse(success, if (success) "Успешно" else "Ошибка"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка сервера: ${e.message}")
            }
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
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Логин не найден в токене")

            try {
                val friends = friendUseCase.getFriends(userLogin)
                val jsonFriends = Json.encodeToString(friends)
                call.respond(BaseResponse(true, jsonFriends))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, BaseResponse(false, "Ошибка сервера"))
            }
        }

        get("/api/v1/friends/pending") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Токен недействителен")

            val userLogin = principal.payload.getClaim("login").asString()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Логин не найден")

            try {
                val pendingRequests = friendUseCase.getPendingRequests(userLogin)
                val jsonData = Json.encodeToString(pendingRequests)
                call.respond(BaseResponse(true, jsonData))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка сервера")
            }
        }

    }
}
