package com.mateo.server.mock.utils

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
class AuthenticationEntryPointJwt : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: org.springframework.security.core.AuthenticationException
    ) =  with(LoggerFactory.getLogger(AuthenticationEntryPointJwt::class.java)) {
        error("Unauthorized error -> ${authException.message}")
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val body: MutableMap<String, Any> = HashMap()
        body[AUTH_STATUS_KEY] = HttpServletResponse.SC_UNAUTHORIZED
        body[AUTH_ERROR_KEY] = AUTH_ERROR_VALUE
        body[AUTH_PATH_KEY] = request.servletPath
        authException.message?.let { body[AUTH_MESSAGE_KEY] = it }
        ObjectMapper().writeValue(response.outputStream, body)
    }

    companion object {
        const val AUTH_STATUS_KEY = "status"
        const val AUTH_ERROR_KEY = "error"
        const val AUTH_PATH_KEY = "path"
        const val AUTH_MESSAGE_KEY = "message"
        const val AUTH_ERROR_VALUE = "Unauthorized"
    }
}
