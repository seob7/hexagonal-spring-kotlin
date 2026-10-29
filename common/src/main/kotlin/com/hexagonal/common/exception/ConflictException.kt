package com.hexagonal.common.exception

import io.github.seob7.BaseErrorCode

/**
 * 리소스의 현재 상태와 충돌하는 요청일 때 던지는 예외. (중복 생성 등)
 *
 * [com.hexagonal.common.exception.handler.ConflictExceptionHandler] 가
 * [BaseErrorCode] 의 HTTP 상태와 메시지로 공통 에러 응답을 만든다.
 *
 * 사용 예: `throw ConflictException(ConflictErrorCode.DUPLICATE_EMAIL)`
 */
class ConflictException(
    val baseErrorCode: BaseErrorCode,
    cause: Throwable? = null,
) : RuntimeException(baseErrorCode.message, cause)
