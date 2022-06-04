package com.mateo.server.mock.model.authentication

import javax.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @NotBlank val refreshToken: String
)
