package com.hexagonal.common.exception

import io.github.seob7.BaseErrorCode

/**
 * 요청한 리소스를 찾을 수 없을 때 던지는 예외.
 *
 * 도메인/영속성 계층에서 조회 실패 시 [NotFoundException] 을 던지면
 * [com.hexagonal.common.exception.handler.NotFoundExceptionHandler] 가
 * [BaseErrorCode] 의 HTTP 상태와 메시지로 공통 에러 응답을 만든다.
 *
 * 사용 예: `throw NotFoundException(NotFoundErrorCode.REFERENCE_NOT_FOUND)`
 */
class NotFoundException(
    val baseErrorCode: BaseErrorCode,
    cause: Throwable? = null,
) : RuntimeException(baseErrorCode.message, cause)