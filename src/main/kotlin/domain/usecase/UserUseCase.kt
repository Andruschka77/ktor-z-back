package com.example.domain.usecase

import com.auth0.jwt.JWTVerifier
import com.example.authentification.JwtService
import com.example.data.model.UserModel
import com.example.data.repository.UserRepositoryImpl

class UserUseCase(
    private val repositoryImpl: UserRepositoryImpl,
    private val jwtService: JwtService
) {
    suspend fun createUser(userModel: UserModel) = repositoryImpl.insertUser(userModel = userModel)

    suspend fun findUserByEmail(email: String) = repositoryImpl.getUserByEmail(email = email)

    fun generateToken(userModel: UserModel): String = jwtService.generateToken(user = userModel)

    fun getJwtVerifier(): JWTVerifier = jwtService.getVerifier()
}