package com.hexagonal.common.exception.handler

import com.hexagonal.common.exception.ForbiddenException
import io.github.seob7.Api
import io.github.seob7.BaseErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * [ForbiddenException] 을 공통 에러 응답(`Api.ERROR`)으로 변환하는 전역 핸들러.
 *
 * Spring Security 필터에서 발생한 인가 실패도 security-common 의 AccessDeniedHandler 가
 * [ForbiddenException] 으로 바꿔 이 핸들러로 보낸다.
 */
@RestControllerAdvice
class ForbiddenExceptionHandler {

    private val log = LoggerFactory.getLogger(ForbiddenExceptionHandler::class.java)

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(e: ForbiddenException): ResponseEntity<Api<BaseErrorCode>> {
        log.warn("Forbidden: {}", e.baseErrorCode)
        return Api.ERROR(e.baseErrorCode)
    }

}
