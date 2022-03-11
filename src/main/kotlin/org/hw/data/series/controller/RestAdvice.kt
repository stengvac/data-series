package org.hw.data.series.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestAdvice {

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(ex: Throwable): ResponseEntity<String> {
        log.warn("Unexpected ex:", ex)

        return ResponseEntity.internalServerError().body("Internal server error")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<String> {
        log.warn("Validation error:", ex)

        return ResponseEntity.badRequest().body(ex.message)
    }

    companion object {
        private val log = LoggerFactory.getLogger(RestAdvice::class.java)
    }
}