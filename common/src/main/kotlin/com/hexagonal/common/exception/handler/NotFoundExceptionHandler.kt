package com.hexagonal.common.exception.handler

import com.hexagonal.common.exception.NotFoundException
import io.github.seob7.Api
import io.github.seob7.BaseErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * [NotFoundException] 을 공통 에러 응답(`Api.ERROR`)으로 변환하는 전역 핸들러.
 *
 * common 모듈에 있으므로, 사용하는 애플리케이션은 컴포넌트 스캔 범위에
 * `com.hexagonal.common` 을 포함해야 한다. (`@SpringBootApplication(scanBasePackages = ["com.hexagonal"])`)
 */
@RestControllerAdvice
class NotFoundExceptionHandler {

    private val log = LoggerFactory.getLogger(NotFoundExceptionHandler::class.java)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<Api<BaseErrorCode>> {
        log.warn("Resource not found: {}", e.baseErrorCode, e)
        return Api.ERROR(e.baseErrorCode)
    }

}