package com.mateo.server.mock.service.authentication

import com.mateo.server.mock.config.error.MockServerExceptions
import com.mateo.server.mock.entity.authentication.RefreshToken
import com.mateo.server.mock.repository.authentication.RefreshTokenRepository
import com.mateo.server.mock.repository.authentication.UserRepository
import com.mateo.server.mock.utils.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class RefreshTokenService constructor(
    @Autowired private val refreshTokenRepository: RefreshTokenRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val jwtUtils: JwtUtils,
    @Autowired private val env: Environment
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
    fun removeUser(userName: String) {
        userRepository.findByUsername(userName)?.let { userEntity ->
            refreshTokenRepository.deleteByUserEntity(userEntity)
            userEntity.id?.let { userRepository.deleteById(it) }
        }
    }

    fun replaceRefreshToken(currentRefreshToken: RefreshToken?) : RefreshToken {
        val currentToken = verifyExpiration(currentRefreshToken)
        currentToken.userEntity?.id?.let { userId ->
            val newToken = createRefreshToken(userId)
            refreshTokenRepository.delete(currentToken)
            refreshTokenRepository.save(newToken)
            return newToken
        } ?: throw MockServerExceptions.GeneralException("invalid refresh token")
    }

    fun verifyExpiration(token: RefreshToken?): RefreshToken {
        if (token == null || token.expiryDate?.isBefore(Instant.now()) == true) {
            token?.let {refreshTokenRepository.delete(it) }
            throw MockServerExceptions.RefreshTokenExpiredException
        }
        return token
    }

    @Transactional
    fun deleteByUserId(userId: Long): Int {
        return refreshTokenRepository.deleteByUserEntity(userRepository.findById(userId).get())
    }
}