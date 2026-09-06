package com.hexagonal.reference.adapter.`in`.dto

import com.hexagonal.reference.domain.Reference
import java.time.LocalDateTime

data class ReferenceResponse(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
) {

    companion object {

        fun from(reference: Reference): ReferenceResponse =
            ReferenceResponse(
                id = requireNotNull(reference.id),
                title = reference.title,
                createdAt = requireNotNull(reference.createdAt),
                updatedAt = requireNotNull(reference.updatedAt),
                deletedAt = reference.deletedAt,
            )
    }
}
