package com.hexagonal.common.exception.errorcode

import io.github.seob7.BaseErrorCode
import org.springframework.http.HttpStatus

/**
 * "권한 없음" 계열 에러 코드. HTTP 상태는 항상 403.
 */
enum class ForbiddenErrorCode(
    private val httpStatus: HttpStatus,
    private val message: String,
) : BaseErrorCode {

    FORBIDDEN(HttpStatus.FORBIDDEN, "Access is denied."),
    ;

    override fun getHttpStatus(): HttpStatus = httpStatus
    override fun getMessage(): String = message

}
