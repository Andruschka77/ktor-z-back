package com.example.domain.usecase

import com.example.domain.repository.FriendRepository

class FriendUseCase(private val repository: FriendRepository) {
    suspend fun sendRequest(senderLogin: String, receiverLogin: String) =
        repository.sendRequest(senderLogin, receiverLogin)

    suspend fun respondToRequest(senderLogin: String, receiverLogin: String, accept: Boolean) =
        repository.respondToRequest(senderLogin, receiverLogin, accept)

    suspend fun getFriends(userLogin: String) =
        repository.getFriends(userLogin)

    suspend fun removeFriend(userLogin: String, friendLogin: String) =
        repository.removeFriend(userLogin, friendLogin)
}