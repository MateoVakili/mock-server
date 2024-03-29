package com.mateo.server.mock.service.authentication

import com.mateo.server.mock.config.error.MockServerExceptions
import com.mateo.server.mock.entity.authentication.RefreshToken
import com.mateo.server.mock.repository.authentication.RefreshTokenRepository
import com.mateo.server.mock.repository.authentication.UserRepository
import com.mateo.server.mock.service.authentication.utils.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class RefreshTokenService @Autowired constructor(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtUtils: JwtUtils,
    private val env: Environment
) {

    private val refreshTokenDurationMs by lazy { env.getProperty("mockserver.jwtRefreshExpirationMs")?.toLong() ?: 0L }

    fun findByToken(token: String) = refreshTokenRepository.findByToken(token)

    fun createRefreshToken(userId: Long) = RefreshToken().apply {
        userEntity = userRepository.findById(userId).get()
        expiryDate = Instant.now().plusMillis(refreshTokenDurationMs)
        token = jwtUtils.generateRefreshToken()
        return refreshTokenRepository.save(this)
    }
    
    @Transactional
    fun replaceRefreshToken(currentRefreshToken: RefreshToken?) : RefreshToken {
        val currentToken = verifyExpiration(currentRefreshToken)
        currentToken.userEntity?.id?.let { userId ->
            refreshTokenRepository.delete(currentToken)
            return refreshTokenRepository.save(createRefreshToken(userId))
        } ?: throw MockServerExceptions.GeneralException("invalid refresh token")
    }

    fun verifyExpiration(token: RefreshToken?): RefreshToken {
        if (token == null || token.expiryDate?.isBefore(Instant.now()) == true) {
            token?.let {refreshTokenRepository.delete(it) }
            throw MockServerExceptions.RefreshTokenExpiredException
        }
        return token
    }
}