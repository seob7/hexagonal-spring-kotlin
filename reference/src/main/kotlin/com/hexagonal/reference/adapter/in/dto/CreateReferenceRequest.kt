package com.hexagonal.reference.adapter.`in`.dto

import com.hexagonal.reference.application.port.`in`.dto.CreateReferenceCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateReferenceRequest(
    @field:NotBlank(message = "레퍼런스 제목은 비어 있을 수 없습니다.")
    @field:Size(max = 100, message = "레퍼런스 제목은 100자 이하여야 합니다.")
    val title: String,

    @field:NotBlank(message = "레퍼런스 내용은 비어 있을 수 없습니다.")
    @field:Size(max = 2_048, message = "레퍼런스 내용은 2,048자 이하여야 합니다.")
    val content: String
) {
    fun toCommand(): CreateReferenceCommand = CreateReferenceCommand(title, content)
}
