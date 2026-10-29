package com.hexagonal.user.adapter.`in`.dto

import com.hexagonal.user.application.port.`in`.dto.LoginCommand
import jakarta.validation.constraints.NotBlank

// 형식 규칙(길이·패턴)은 검사하지 않는다. 규칙이 바뀌기 전에 가입한 회원도 로그인할 수 있어야 한다.
data class LoginRequest(
    @field:NotBlank(message = "이메일은 비어 있을 수 없습니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    val password: String,
) {
    fun toCommand(): LoginCommand = LoginCommand(email, password)
}
