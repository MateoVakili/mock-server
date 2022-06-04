package com.mateo.server.mock.config.error

import org.springframework.http.HttpStatus

data class MockServerApiError(
    var status: HttpStatus,
    var error: String,
    var errorDescription: String
)