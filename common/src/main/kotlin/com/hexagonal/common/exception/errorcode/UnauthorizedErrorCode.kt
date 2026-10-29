package com.hexagonal.common.exception.errorcode

import io.github.seob7.BaseErrorCode
import org.springframework.http.HttpStatus

/**
 * "인증 실패" 계열 에러 코드. HTTP 상태는 항상 401.
 *
 * 클라이언트는 `INVALID_TOKEN` 이면 Refresh Token 으로 재발급을,
 * `INVALID_REFRESH_TOKEN` 이면 재로그인을 시도한다.
 */
enum class UnauthorizedErrorCode(
    private val httpStatus: HttpStatus,
    private val message: String,
) : BaseErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication is required."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Access token is invalid or expired."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Email or password is incorrect."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh token is invalid or expired."),
    ;

    override fun getHttpStatus(): HttpStatus = httpStatus
    override fun getMessage(): String = message

}
