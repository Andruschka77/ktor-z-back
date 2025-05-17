package com.example.plugins

import com.example.domain.repository.UserRepository
import com.example.domain.usecase.FriendUseCase
import com.example.domain.usecase.UserUseCase
import com.example.routes.FriendRoute
import com.example.routes.UserRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(userUseCase: UserUseCase, friendUseCase: FriendUseCase) {
    routing {
        UserRoute(userUseCase = userUseCase)
        FriendRoute(friendUseCase = friendUseCase)
    }
}
