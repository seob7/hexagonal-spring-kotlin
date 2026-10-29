package com.hexagonal.user.adapter.`in`.dto

import com.hexagonal.user.application.port.`in`.dto.SignUpCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignUpRequest(
    @field:NotBlank(message = "이메일은 비어 있을 수 없습니다.")
    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    @field:Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    @field:Size(
        min = PasswordRule.MIN_LENGTH,
        max = PasswordRule.MAX_LENGTH,
        message = "비밀번호는 {min}자 이상 {max}자 이하여야 합니다.",
    )
    @field:Pattern(regexp = PasswordRule.PATTERN, message = "비밀번호는 영문·숫자·특수문자로만 구성해야 합니다.")
    val password: String,

    @field:NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
    @field:Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
    val nickname: String,
) {
    fun toCommand(): SignUpCommand = SignUpCommand(email, password, nickname)
}
