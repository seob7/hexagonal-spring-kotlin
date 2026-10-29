package com.hexagonal.user.adapter.out.persistence.repository

import com.hexagonal.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByIdAndDeletedAtIsNull(userId: Long): User?

    fun findByEmailAndDeletedAtIsNull(email: String): User?

    fun existsByEmail(email: String): Boolean

}
