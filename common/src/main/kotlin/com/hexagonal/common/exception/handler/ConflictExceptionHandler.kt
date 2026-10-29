package com.hexagonal.common.exception.handler

import com.hexagonal.common.exception.ConflictException
import io.github.seob7.Api
import io.github.seob7.BaseErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * [ConflictException] 을 공통 에러 응답(`Api.ERROR`)으로 변환하는 전역 핸들러.
 */
@RestControllerAdvice
class ConflictExceptionHandler {

    private val log = LoggerFactory.getLogger(ConflictExceptionHandler::class.java)

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(e: ConflictException): ResponseEntity<Api<BaseErrorCode>> {
        log.warn("Conflict: {}", e.baseErrorCode, e)
        return Api.ERROR(e.baseErrorCode)
    }

}
