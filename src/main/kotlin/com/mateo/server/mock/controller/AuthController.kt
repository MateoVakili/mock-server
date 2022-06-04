package com.mateo.server.mock.controller

import com.mateo.server.mock.config.error.MockServerExceptions
import com.mateo.server.mock.entity.Role
import com.mateo.server.mock.entity.RoleType
import com.mateo.server.mock.entity.Userentity
import com.mateo.server.mock.model.*
import com.mateo.server.mock.repository.UserRepository
import com.mateo.server.mock.service.RefreshTokenService
import com.mateo.server.mock.service.UserDetailsImpl
import com.mateo.server.mock.utils.AuthenticationEntryPointJwt
import com.mateo.server.mock.utils.JwtUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/authentication")
class AuthController {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Autowired
    lateinit var refreshTokenService: RefreshTokenService

    private val logger = LoggerFactory.getLogger(AuthenticationEntryPointJwt::class.java)

    @PostMapping("/login")
    fun authenticateUser(@RequestBody loginRequest: @Valid RegistrationRequest): ResponseEntity<*> {
        val authentication = authenticationManager
            .authenticate(UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password))
        SecurityContextHolder.getContext().authentication = authentication
        (authentication.principal as? UserDetailsImpl)?.let { userDetails ->
            val (_, _, token) = refreshTokenService.createRefreshToken(userDetails.id)
            token?.let { refreshToken ->
                logger.info("Refresh token: $refreshToken was created for user: ${loginRequest.username}")
                return ResponseEntity.ok(
                    SignupResponse(
                        accessToken = jwtUtils.generateJwtToken(userDetails),
                        refreshToken = refreshToken,
                        id = userDetails.id,
                        username = userDetails.username,
                        email = userDetails.email,
                        roles = userDetails.authorities.stream().map { item: GrantedAuthority? -> item!!.authority }
                            .collect(Collectors.toList())
                    )
                )
            } ?: throw MockServerExceptions.RefreshTokenGenerationException
        } ?:  throw MockServerExceptions.RefreshTokenGenerationException
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody signUpRequest: @Valid SignupRequest): ResponseEntity<*> {
        if (userRepository.existsByUsername(signUpRequest.username)) {
            throw MockServerExceptions.UserNameAlreadyTakenException(signUpRequest.username)
        }
        if (userRepository.existsByEmail(signUpRequest.email)) {
            throw MockServerExceptions.EmailAlreadyTakenException(signUpRequest.email)
        }
        userRepository.save<Userentity>(
            Userentity(
                username = signUpRequest.username,
                email = signUpRequest.email,
                password = encoder.encode(signUpRequest.password),
                roles = hashSetOf(Role(name = RoleType.ROLE_USER, id = 1))
            )
        )
        return ResponseEntity.noContent().build<Any>()
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody request: @Valid RefreshTokenRequest): ResponseEntity<*> {
        val token = refreshTokenService.findByToken(request.refreshToken)
        refreshTokenService.replaceRefreshToken(token).let { newRefreshToken ->
            val username = newRefreshToken.userEntity?.username
            val refreshToken = newRefreshToken.token
            if(username != null && refreshToken != null) {
                return ResponseEntity.ok(
                    TokenRefreshResponse(
                        accessToken = jwtUtils.generateTokenFromUsername(username),
                        refreshToken = refreshToken
                    )
                )
            } else throw MockServerExceptions.GeneralException("Something went wrong with refresh token!")
        }
    }
}
