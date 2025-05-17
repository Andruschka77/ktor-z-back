package com.example.data.model.requests

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val senderLogin: String
)
