package com.hexagonal.user.fake

import com.hexagonal.common.exception.NotFoundException
import com.hexagonal.common.exception.errorcode.NotFoundErrorCode
import com.hexagonal.user.application.port.out.UserPersistencePort
import com.hexagonal.user.domain.User
import org.springframework.test.util.ReflectionTestUtils

/** 인메모리 [UserPersistencePort]. 식별자는 JPA(TSID) 대신 순번으로 채운다. */
class FakeUserPersistencePort : UserPersistencePort {

    private val users = mutableMapOf<Long, User>()
    private var sequence = 0L

    override fun create(user: User): User {
        ReflectionTestUtils.setField(user, "id", ++sequence)
        users[sequence] = user
        return user
    }

    override fun findUser(userId: Long): User =
        users[userId]?.takeIf { it.deletedAt == null }
            ?: throw NotFoundException(NotFoundErrorCode.USER_NOT_FOUND)

    override fun getUserByEmail(email: String): User? =
        users.values.find { it.email == email && it.deletedAt == null }

    override fun existsByEmail(email: String): Boolean =
        users.values.any { it.email == email }

}
