package com.mateo.server.mock.config.authentication

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class ServerPasswordEncoder {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return Argon2PasswordEncoder()
    }
}