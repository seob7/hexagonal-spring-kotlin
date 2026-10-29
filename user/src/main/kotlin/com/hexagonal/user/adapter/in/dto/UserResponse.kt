package com.hexagonal.user.adapter.`in`.dto

import com.hexagonal.user.domain.User
import com.hexagonal.user.domain.UserRole
import java.time.LocalDateTime

// 비밀번호 해시는 절대 응답에 담지 않는다.
data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: UserRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    companion object {

        fun from(user: User): UserResponse =
            UserResponse(
                id = requireNotNull(user.id),
                email = user.email,
                nickname = user.nickname,
                role = user.role,
                createdAt = requireNotNull(user.createdAt),
                updatedAt = requireNotNull(user.updatedAt),
            )
    }
}
