package com.mateo.server.mock.model.authentication

data class SigninResponse(
    val accessToken: String,
    val refreshToken: String,
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
)
