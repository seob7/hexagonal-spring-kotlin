package com.hexagonal.user.adapter.`in`.dto

import com.hexagonal.user.application.port.`in`.dto.ReissueCommand
import jakarta.validation.constraints.NotBlank

data class ReissueRequest(
    @field:NotBlank(message = "Refresh Token 은 비어 있을 수 없습니다.")
    val refreshToken: String,
) {
    fun toCommand(): ReissueCommand = ReissueCommand(refreshToken)
}
