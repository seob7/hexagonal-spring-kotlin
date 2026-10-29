package com.hexagonal.user.adapter.`in`.dto

import com.hexagonal.user.application.port.`in`.dto.LogoutCommand
import jakarta.validation.constraints.NotBlank

data class LogoutRequest(
    @field:NotBlank(message = "Refresh Token 은 비어 있을 수 없습니다.")
    val refreshToken: String,
) {
    fun toCommand(): LogoutCommand = LogoutCommand(refreshToken)
}
