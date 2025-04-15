package com.example.authentification

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.UserModel

class JwtService {
    private val issuer = "z-server"
    private val jwtSecret  = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: UserModel): String {
        return JWT.create()
            .withSubject("ZAppAuthentication")
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .sign(algorithm)
    }

    fun getVerifier(): JWTVerifier = verifier
}