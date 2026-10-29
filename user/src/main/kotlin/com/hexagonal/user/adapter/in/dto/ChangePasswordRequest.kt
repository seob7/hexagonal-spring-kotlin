package com.hexagonal.user.adapter.`in`.dto

import com.hexagonal.user.application.port.`in`.dto.ChangePasswordCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ChangePasswordRequest(
    @field:NotBlank(message = "현재 비밀번호는 비어 있을 수 없습니다.")
    val currentPassword: String,

    @field:NotBlank(message = "새 비밀번호는 비어 있을 수 없습니다.")
    @field:Size(
        min = PasswordRule.MIN_LENGTH,
        max = PasswordRule.MAX_LENGTH,
        message = "새 비밀번호는 {min}자 이상 {max}자 이하여야 합니다.",
    )
    @field:Pattern(regexp = PasswordRule.PATTERN, message = "새 비밀번호는 영문·숫자·특수문자로만 구성해야 합니다.")
    val newPassword: String,
) {
    fun toCommand(): ChangePasswordCommand = ChangePasswordCommand(currentPassword, newPassword)
}
