package com.hexagonal.common.exception.handler

import com.hexagonal.common.exception.UnauthorizedException
import io.github.seob7.Api
import io.github.seob7.BaseErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * [UnauthorizedException] 을 공통 에러 응답(`Api.ERROR`)으로 변환하는 전역 핸들러.
 *
 * Spring Security 필터에서 발생한 인증 실패도 security-common 의 EntryPoint 가
 * [UnauthorizedException] 으로 바꿔 이 핸들러로 보낸다.
 */
@RestControllerAdvice
class UnauthorizedExceptionHandler {

    private val log = LoggerFactory.getLogger(UnauthorizedExceptionHandler::class.java)

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<Api<BaseErrorCode>> {
        // 토큰 만료처럼 정상 흐름에서도 자주 발생하므로 스택 트레이스는 남기지 않는다.
        log.warn("Unauthorized: {}", e.baseErrorCode)
        return Api.ERROR(e.baseErrorCode)
    }

}
