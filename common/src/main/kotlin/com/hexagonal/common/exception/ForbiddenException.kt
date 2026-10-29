package com.hexagonal.common.exception

import io.github.seob7.BaseErrorCode

/**
 * 인증은 되었지만 권한이 없을 때 던지는 예외.
 *
 * [com.hexagonal.common.exception.handler.ForbiddenExceptionHandler] 가
 * [BaseErrorCode] 의 HTTP 상태와 메시지로 공통 에러 응답을 만든다.
 *
 * 사용 예: `throw ForbiddenException(ForbiddenErrorCode.FORBIDDEN)`
 */
class ForbiddenException(
    val baseErrorCode: BaseErrorCode,
    cause: Throwable? = null,
) : RuntimeException(baseErrorCode.message, cause)
