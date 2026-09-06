package com.hexagonal.common.exception

import io.github.seob7.BaseErrorCode

/**
 * 요청 DTO 검증 실패 시 던지는 예외.
 *
 * Bean Validation(`@Valid`) 검증 실패 또는 비즈니스 로직상 유효하지 않은 요청일 때
 * [ValidationException] 을 던지면
 * [com.hexagonal.common.exception.handler.ValidationExceptionHandler] 가
 * [BaseErrorCode] 의 HTTP 상태와 메시지로 공통 에러 응답을 만든다.
 *
 * 사용 예: `throw ValidationException(ValidationErrorCode.REQUEST_VALIDATION_FAILED)`
 */
class ValidationException(
    val baseErrorCode: BaseErrorCode,
    cause: Throwable? = null,
) : RuntimeException(baseErrorCode.message, cause)