package com.example.domain.usecase

import com.example.domain.repository.FriendRepository
import com.example.domain.repository.UserRepository

class FriendUseCase(private val friendRepository: FriendRepository, private val userRepository: UserRepository) {
    suspend fun sendRequest(senderLogin: String, receiverLogin: String): Boolean {
        if (senderLogin == receiverLogin) throw IllegalArgumentException("Нельзя отправить запрос себе")
        return friendRepository.sendRequest(senderLogin, receiverLogin)
    }

    suspend fun getFriends(userLogin: String) =
        friendRepository.getFriends(userLogin)

    suspend fun removeFriend(userLogin: String, friendLogin: String) =
        friendRepository.removeFriend(userLogin, friendLogin)

    suspend fun respondToRequest(senderLogin: String, receiverLogin: String, accept: Boolean): Boolean {
        val success = friendRepository.respondToRequest(senderLogin, receiverLogin, accept)
        return success
    }

    suspend fun getPendingRequests(userLogin: String) =
        friendRepository.getPendingRequests(userLogin)
}