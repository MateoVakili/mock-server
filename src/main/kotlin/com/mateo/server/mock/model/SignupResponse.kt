package com.mateo.server.mock.model

data class SignupResponse(
    val accessToken: String,
    val refreshToken: String,
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
) {
    companion object {
        const val TOKEN_TYPE = "Bearer"
    }
}