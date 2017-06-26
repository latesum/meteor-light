package com.latesum.meteorlight.controller

import com.google.gson.JsonObject
import com.latesum.meteorlight.exception.ServiceException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ErrorController : ResponseEntityExceptionHandler() {

    private fun getErrorJson(msg: String?): String {
        return JsonObject().addProperty("error",msg).toString()
    }

    @ExceptionHandler(value = ServiceException::class)
    fun monitorError(e: ServiceException, request: WebRequest): ResponseEntity<Any> {
        when (e.type) {
            ServiceException.ExceptionType.PERMISSION_DENIED ->
                return handleExceptionInternal(
                        e,
                        getErrorJson(e.type.name),
                        HttpHeaders(),
                        HttpStatus.FORBIDDEN,
                        request)
            ServiceException.ExceptionType.INVALID_ARGUMENT ->
                return handleExceptionInternal(
                        e,
                        getErrorJson(e.message),
                        HttpHeaders(),
                        HttpStatus.OK,
                        request)
            else ->
                return handleExceptionInternal(
                        e,
                        getErrorJson(e.message),
                        HttpHeaders(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request)
        }
    }

    @ExceptionHandler(value = Exception::class)
    fun unknownError(e: Exception, request: WebRequest): ResponseEntity<Any> {
        val rspJson = JsonObject().addProperty("error", e.message).toString()
        return handleExceptionInternal(e, rspJson, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request)
    }

}
