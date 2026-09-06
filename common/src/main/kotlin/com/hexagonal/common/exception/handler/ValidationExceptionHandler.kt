package com.hexagonal.common.exception.handler

import com.hexagonal.common.exception.ValidationException
import com.hexagonal.common.exception.errorcode.ValidationErrorCode
import io.github.seob7.Api
import io.github.seob7.BaseErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 요청 검증 실패 예외들을 공통 에러 응답(`Api.ERROR`)으로 변환하는 전역 핸들러.
 *
 * 처리 대상:
 * 1. [ValidationException] — 비즈니스 로직에서 명시적으로 던진 검증 실패
 * 2. [MethodArgumentNotValidException] — `@Valid` 어노테이션의 Bean Validation 실패
 *
 * common 모듈에 있으므로, 사용하는 애플리케이션은 컴포넌트 스캔 범위에
 * `com.hexagonal.common` 을 포함해야 한다. (`@SpringBootApplication(scanBasePackages = ["com.hexagonal"])`)
 */
@RestControllerAdvice
class ValidationExceptionHandler {

    private val log = LoggerFactory.getLogger(ValidationExceptionHandler::class.java)

    /**
     * [ValidationException] 처리.
     * 비즈니스 로직에서 명시적으로 던진 검증 실패 예외.
     */
    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(e: ValidationException): ResponseEntity<Api<BaseErrorCode>> {
        log.warn("Validation failed: {}", e.baseErrorCode, e)
        return Api.ERROR(e.baseErrorCode)
    }

    /**
     * [MethodArgumentNotValidException] 처리.
     * `@Valid` 어노테이션의 Bean Validation 실패 예외.
     * Spring 이 자동으로 던지는 예외로, 요청 DTO 의 필드 검증 실패 시 발생한다.
     * 모든 검증 실패 메시지를 수집해서 응답에 포함한다.
     *
     * 예시: 한 필드에 @NotBlank, @Size 두 검증이 모두 실패하면
     * "레퍼런스 내용은 비어 있을 수 없습니다./n레퍼런스 내용은 2,048자 이하여야 합니다."
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<Api<String>> {
        val bindingResult = e.bindingResult
        val customMessage = bindingResult.fieldErrors
            .mapNotNull { it.defaultMessage }
            .joinToString("/n")
            .takeIf { it.isNotEmpty() }
            ?: ValidationErrorCode.REQUEST_VALIDATION_FAILED.message

        log.warn("Request validation failed: {}", customMessage, e)

        // 커스텀 메시지를 포함한 에러 응답 생성
        return Api.ERROR(customMessage, ValidationErrorCode.REQUEST_VALIDATION_FAILED.httpStatus)
    }

}