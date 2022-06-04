package com.mateo.server.mock.config.authentication

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo.server.mock.config.error.MockServerApiError
import org.springframework.http.HttpStatus

@Component
class AuthenticationEntryPointJwt : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: org.springframework.security.core.AuthenticationException
    ) = with(LoggerFactory.getLogger(AuthenticationEntryPointJwt::class.java)) {
        error("Unauthorized error -> ${authException.message}")
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        ObjectMapper().writeValue(
            response.outputStream,
            MockServerApiError(
                status = HttpStatus.UNAUTHORIZED,
                error = "unauthorized access to: ${request.servletPath}, 'access_token' missing in header or is invalid",
                errorDescription = authException.message.toString()
            )
        )
    }
}
