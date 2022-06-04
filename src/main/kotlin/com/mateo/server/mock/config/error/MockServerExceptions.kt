package com.mateo.server.mock.config.error

sealed class MockServerExceptions : Exception() {
    data class GeneralException(val description: String) : MockServerExceptions()
    object RefreshTokenGenerationException : MockServerExceptions()
    object RefreshTokenExpiredException : MockServerExceptions()
    data class UserNameAlreadyTakenException(val userName: String) : MockServerExceptions()
    data class EmailAlreadyTakenException(val email: String) : MockServerExceptions()
}