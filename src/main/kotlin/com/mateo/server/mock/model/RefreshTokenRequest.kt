package com.mateo.server.mock.model

import javax.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @NotBlank val refreshToken: String
)
