package com.mateo.server.mock.model.authentication

import javax.validation.constraints.NotBlank

data class RegistrationRequest(
    @NotBlank
    var username: String,
    @NotBlank
    var password: String
)