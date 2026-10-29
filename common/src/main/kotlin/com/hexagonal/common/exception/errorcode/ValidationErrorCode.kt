package com.hexagonal.common.exception.errorcode

import io.github.seob7.BaseErrorCode
import org.springframework.http.HttpStatus

/**
 * "요청 값 검증 실패" 계열 에러 코드.
 *
 * 요청 DTO 의 `@Valid` 검증 실패 시 `REQUEST_VALIDATION_FAILED` 를 사용하고,
 * 비즈니스 로직상 유효하지 않은 요청은 원인을 드러내는 상수를 추가한다. HTTP 상태는 항상 400.
 */
enum class ValidationErrorCode(
    private val httpStatus: HttpStatus,
    private val message: String,
) : BaseErrorCode {

    REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Request validation failed."),
    CURRENT_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "Current password does not match."),
    ;

    override fun getHttpStatus(): HttpStatus = httpStatus
    override fun getMessage(): String = message

}