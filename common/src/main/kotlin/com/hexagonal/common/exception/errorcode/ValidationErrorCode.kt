package com.hexagonal.common.exception.errorcode

import io.github.seob7.BaseErrorCode
import org.springframework.http.HttpStatus

/**
 * "요청 값 검증 실패" 계열 에러 코드.
 *
 * 요청 DTO 의 `@Valid` 검증 실패 시 이 에러 코드를 사용한다.
 * 네이밍은 `REQUEST_VALIDATION_FAILED`, HTTP 상태는 항상 400.
 */
enum class ValidationErrorCode(
    private val httpStatus: HttpStatus,
    private val message: String,
) : BaseErrorCode {

    REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Request validation failed."),
    ;

    override fun getHttpStatus(): HttpStatus = httpStatus
    override fun getMessage(): String = message

}