package com.example.data.model.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val login: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val isActive: Boolean = false
)
