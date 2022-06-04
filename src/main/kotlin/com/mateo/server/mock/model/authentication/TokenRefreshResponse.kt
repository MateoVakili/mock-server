package com.mateo.server.mock.model.authentication

data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String
)
