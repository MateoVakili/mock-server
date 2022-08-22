package com.mateo.server.mock.service.authentication.utils

import com.mateo.server.mock.model.authentication.UserDetailsImpl
import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils constructor(
    @Autowired private val env: Environment
) {
    private val accs by lazy { env.getProperty("mockserver.accs") }
    private val rfs by lazy { env.getProperty("mockserver.rfs") }
    private val jwtExpirationMs by lazy { env.getProperty("mockserver.jwtExpirationMs")?.toInt() ?: 0 }
    private val logger = LoggerFactory.getLogger(JwtUtils::class.java)

    fun generateRefreshToken(): String {
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, rfs)
            .setIssuedAt(Date())
            .compact()
    }
    fun generateJwtAccessToken(userPrincipal: UserDetailsImpl): String {
        return generateAccessTokenFromUsername(userPrincipal.username)
    }

    fun generateAccessTokenFromUsername(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, accs)
            .compact()
    }

    fun getUserNameFromJwtAccessToken(token: String?): String =
        Jwts.parser().setSigningKey(accs).parseClaimsJws(token).body.subject

    fun validateJwtAccessToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(accs).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.debug("JWT claims string is empty: {}", e.message)
        }
        return false
    }
}
