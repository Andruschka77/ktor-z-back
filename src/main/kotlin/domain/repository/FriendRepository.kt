package com.example.domain.repository

import com.example.data.model.FriendModel

interface FriendRepository {
    suspend fun sendRequest(senderLogin: String, receiverLogin: String): Boolean

    suspend fun respondToRequest(senderLogin: String, receiverLogin: String, accept: Boolean): Boolean

    suspend fun getFriends(userLogin: String): List<FriendModel>

    suspend fun removeFriend(userLogin: String, friendLogin: String): Boolean
}