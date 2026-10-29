package com.hexagonal.user.adapter.`in`.dto

/**
 * 비밀번호 형식 규칙. 가입·변경 요청이 같은 규칙을 쓴다.
 *
 * BCrypt 는 72바이트를 넘는 입력을 거부하므로, 1바이트 문자(출력 가능한 ASCII)로 64자까지만 허용한다.
 */
object PasswordRule {

    const val MIN_LENGTH = 8
    const val MAX_LENGTH = 64
    const val PATTERN = "^[\\x20-\\x7E]+$"

}
