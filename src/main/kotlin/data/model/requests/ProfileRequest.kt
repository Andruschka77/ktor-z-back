package com.example.data.model.requests

import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val firstName: String,
    val lastName: String,
    val login: String,
    val email: String
)
