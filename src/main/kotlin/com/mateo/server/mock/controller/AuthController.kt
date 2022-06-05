package com.mateo.server.mock.controller

import com.mateo.server.mock.config.error.MockServerExceptions
import com.mateo.server.mock.entity.authentication.RoleType
import com.mateo.server.mock.entity.authentication.Userentity
import com.mateo.server.mock.model.authentication.*
import com.mateo.server.mock.repository.authentication.UserRepository
import com.mateo.server.mock.service.authentication.RefreshTokenService
import com.mateo.server.mock.model.authentication.UserDetailsImpl
import com.mateo.server.mock.utils.JwtUtils
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

@CrossOrigin(
    origins = [
        "http://localhost:8080/",
        "https://mock-server-fe.herokuapp.com/"
    ], maxAge = 3600
)
@RestController
@RequestMapping("/authentication")
class AuthController @Autowired constructor(
    val authenticationManager: AuthenticationManager,
    val userRepository: UserRepository,
    val encoder: PasswordEncoder,
    val jwtUtils: JwtUtils,
    val refreshTokenService: RefreshTokenService,
) {
    @PostMapping("/login")
    fun authenticateUser(@RequestBody loginRequest: @Valid RegistrationRequest): ResponseEntity<SigninResponse> {
        val authentication = authenticationManager
            .authenticate(UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password))
        SecurityContextHolder.getContext().authentication = authentication
        (authentication.principal as? UserDetailsImpl)?.let { userDetails ->
            refreshTokenService.createRefreshToken(userDetails.id).token?.let { refreshToken ->
                return ResponseEntity.ok(
                    SigninResponse(
                        accessToken = jwtUtils.generateJwtAccessToken(userDetails),
                        refreshToken = refreshToken,
                        id = userDetails.id,
                        username = userDetails.username,
                        email = userDetails.email,
                        roles = userDetails.authorities.stream().map { item: GrantedAuthority? -> item!!.authority }
                            .collect(Collectors.toList())
                    )
                )
            } ?: throw MockServerExceptions.RefreshTokenGenerationException
        } ?: throw MockServerExceptions.RefreshTokenGenerationException
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody signUpRequest: @Valid SignupRequest): ResponseEntity<*> {
        if (userRepository.existsByUsername(signUpRequest.username)) {
            throw MockServerExceptions.UserNameAlreadyTakenException(signUpRequest.username)
        }
        if (userRepository.existsByEmail(signUpRequest.email)) {
            throw MockServerExceptions.EmailAlreadyTakenException(signUpRequest.email)
        }
        userRepository.save(
            Userentity(
                username = signUpRequest.username,
                email = signUpRequest.email,
                password = encoder.encode(signUpRequest.password),
                role = RoleType.ROLE_USER.name
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
            if (username != null && refreshToken != null) {
                return ResponseEntity.ok(
                    TokenRefreshResponse(
                        accessToken = jwtUtils.generateAccessTokenFromUsername(username),
                        refreshToken = refreshToken
                    )
                )
            } else throw MockServerExceptions.GeneralException("Something went wrong with refresh token!")
        }
    }

    @DeleteMapping("/delete")
    fun refreshToken(@RequestHeader("access_token") token: String): ResponseEntity<*> {
        refreshTokenService.removeUser(jwtUtils.getUserNameFromJwtAccessToken(token))
        return ResponseEntity.ok().build<Any>()
    }
}
