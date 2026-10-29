package com.hexagonal.common.exception

import io.github.seob7.BaseErrorCode

/**
 * 인증에 실패했을 때 던지는 예외. (자격 증명 불일치, 토큰 없음·만료·위조)
 *
 * [com.hexagonal.common.exception.handler.UnauthorizedExceptionHandler] 가
 * [BaseErrorCode] 의 HTTP 상태와 메시지로 공통 에러 응답을 만든다.
 *
 * 사용 예: `throw UnauthorizedException(UnauthorizedErrorCode.INVALID_CREDENTIALS)`
 */
class UnauthorizedException(
    val baseErrorCode: BaseErrorCode,
    cause: Throwable? = null,
) : RuntimeException(baseErrorCode.message, cause)
