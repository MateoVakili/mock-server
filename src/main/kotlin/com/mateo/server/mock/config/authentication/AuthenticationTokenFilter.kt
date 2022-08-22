package com.mateo.server.mock.config.authentication

import com.mateo.server.mock.service.authentication.UserDetailsServiceImpl
import com.mateo.server.mock.service.authentication.utils.JwtUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationTokenFilter constructor(
    @Autowired private val jwtUtils: JwtUtils,
    @Autowired private val userDetailsService: UserDetailsServiceImpl
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = request.getHeader(TOKEN_HEADER_KEY)?.substringAfter(TOKEN_TYPE)
            if (jwtUtils.validateJwtAccessToken(jwt)) {
                val username: String = jwtUtils.getUserNameFromJwtAccessToken(jwt)
                val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
                UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).run {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = this
                }
            }
        } catch (e: Exception) {
            Companion.logger.error("cannot set user authentication: {}", e.message)
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationTokenFilter::class.java)
        private const val TOKEN_TYPE = "Bearer "
        private const val TOKEN_HEADER_KEY = "access_token"
    }
}