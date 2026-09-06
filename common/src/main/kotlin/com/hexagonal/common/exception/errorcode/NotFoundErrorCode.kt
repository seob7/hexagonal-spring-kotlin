package com.hexagonal.common.exception.errorcode

import io.github.seob7.BaseErrorCode
import org.springframework.http.HttpStatus

/**
 * "리소스를 찾을 수 없음" 계열 에러 코드.
 *
 * 새 도메인에서 조회 실패 케이스가 생기면 여기에 상수를 추가한다.
 * 네이밍은 `<도메인>_NOT_FOUND`, HTTP 상태는 항상 404.
 */
enum class NotFoundErrorCode(
    private val httpStatus: HttpStatus,
    private val message: String,
) : BaseErrorCode {

    REFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Reference is not found."),
    ;

    override fun getHttpStatus(): HttpStatus = httpStatus
    override fun getMessage(): String = message

}