package com.mateo.server.mock.config.error

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.http.HttpStatus
import java.util.ArrayList
import org.springframework.web.context.request.WebRequest
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class PostmanMockExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(MockServerExceptions.GeneralException::class)
    protected fun handleGeneralException(
        ex: MockServerExceptions.GeneralException
    ): ResponseEntity<Any> {
        val apiError = MockServerApiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Something went wrong",
            ex.description
        )
        return ResponseEntity<Any>(apiError, HttpHeaders(), apiError.status)
    }

    @ExceptionHandler(MockServerExceptions.RefreshTokenGenerationException::class)
    protected fun handleRequestIdNotFoundException(
        ex: MockServerExceptions.RefreshTokenGenerationException
    ): ResponseEntity<Any> {
        val apiError = MockServerApiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Something went wrong with token generation!",
            "Refresh token generation was unsuccessful"
        )
        return ResponseEntity<Any>(apiError, HttpHeaders(), apiError.status)
    }

    @ExceptionHandler(MockServerExceptions.UserNameAlreadyTakenException::class)
    protected fun handleUserNameAlreadyTakenException(
        ex: MockServerExceptions.UserNameAlreadyTakenException
    ): ResponseEntity<Any> {
        ex.message
        val apiError = MockServerApiError(
            HttpStatus.CONFLICT,
            "username in use",
            "U" +
                    "username: ${ex.userName} is already taken!"
        )
        return ResponseEntity<Any>(apiError, HttpHeaders(), apiError.status)
    }

    @ExceptionHandler(MockServerExceptions.EmailAlreadyTakenException::class)
    protected fun handleEmailAlreadyTakenException(
        ex: MockServerExceptions.EmailAlreadyTakenException
    ): ResponseEntity<Any> {
        val apiError = MockServerApiError(
            HttpStatus.CONFLICT,
            "email in use",
            "email: ${ex.email} is already taken!"
        )
        return ResponseEntity<Any>(apiError, HttpHeaders(), apiError.status)
    }

    @ExceptionHandler(MockServerExceptions.RefreshTokenExpiredException::class)
    protected fun handleRefreshTokenExpiredException(
        ex: MockServerExceptions.RefreshTokenExpiredException
    ): ResponseEntity<Any> {
        val apiError = MockServerApiError(
            HttpStatus.CONFLICT,
            "refresh token is expired!",
            "refresh token is expired! please login again. "
        )
        return ResponseEntity<Any>(apiError, HttpHeaders(), apiError.status)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors: MutableList<String> = ArrayList()
        for (error in ex.bindingResult.fieldErrors) {
            errors.add(error.field + ": " + error.defaultMessage)
        }
        for (error in ex.bindingResult.globalErrors) {
            errors.add(error.objectName + ": " + error.defaultMessage)
        }
        val apiError = MockServerApiError(HttpStatus.BAD_REQUEST, ex.localizedMessage, errors.toString())
        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val error = ex.parameterName + " parameter is missing"
        val apiError = MockServerApiError(HttpStatus.BAD_REQUEST, ex.localizedMessage, error)
        return ResponseEntity<Any>(apiError, HttpHeaders(), apiError.status)
    }

    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val builder = StringBuilder()
        val errors: MutableList<String> = ArrayList()
        builder.append(ex.method)
        builder.append(" method is not supported for this request. Supported methods are ")
        ex.supportedHttpMethods?.forEach { t -> builder.append("$t ") }
        errors.add(builder.toString())
        val apiError = MockServerApiError(
            HttpStatus.METHOD_NOT_ALLOWED,
            ex.localizedMessage,
            builder.toString()
        )
        return ResponseEntity(apiError, HttpHeaders(), apiError.status)
    }

    @ExceptionHandler(Exception::class)
    protected fun handleInternalError(
        ex: Exception
    ): ResponseEntity<Any> {
        val apiError = MockServerApiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "something went wrong",
            ex.message.toString()
        )
        return ResponseEntity<Any>(apiError, HttpHeaders(), apiError.status)
    }
}
