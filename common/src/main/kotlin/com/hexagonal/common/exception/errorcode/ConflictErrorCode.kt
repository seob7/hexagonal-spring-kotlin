package com.hexagonal.common.exception.errorcode

import io.github.seob7.BaseErrorCode
import org.springframework.http.HttpStatus

/**
 * "리소스 상태 충돌" 계열 에러 코드. HTTP 상태는 항상 409.
 * 네이밍은 `DUPLICATE_<대상>` 처럼 충돌 원인을 드러낸다.
 */
enum class ConflictErrorCode(
    private val httpStatus: HttpStatus,
    private val message: String,
) : BaseErrorCode {

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "Email is already in use."),
    ;

    override fun getHttpStatus(): HttpStatus = httpStatus
    override fun getMessage(): String = message

}
