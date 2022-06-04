package com.mateo.server.mock.model

data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        const val TOKEN_TYPE = "Bearer"
    }
}