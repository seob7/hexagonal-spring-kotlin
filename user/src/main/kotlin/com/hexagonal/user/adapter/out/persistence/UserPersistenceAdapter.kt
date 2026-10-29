package com.hexagonal.user.adapter.out.persistence

import com.hexagonal.common.annotation.PersistenceAdapter
import com.hexagonal.common.exception.ConflictException
import com.hexagonal.common.exception.NotFoundException
import com.hexagonal.common.exception.errorcode.ConflictErrorCode
import com.hexagonal.common.exception.errorcode.NotFoundErrorCode
import com.hexagonal.user.adapter.out.persistence.repository.UserRepository
import com.hexagonal.user.application.port.out.UserPersistencePort
import com.hexagonal.user.domain.User
import org.springframework.dao.DataIntegrityViolationException

@PersistenceAdapter
class UserPersistenceAdapter(
    private val userRepository: UserRepository,
) : UserPersistencePort {

    // 서비스의 중복 검사를 동시 가입 요청이 함께 통과하면 unique 제약이 막는다.
    // 즉시 flush 해야 제약 위반을 여기서 잡아 409 로 바꿀 수 있다. (커밋 시점이면 500 이 된다)
    override fun create(user: User): User =
        try {
            userRepository.saveAndFlush(user)
        } catch (e: DataIntegrityViolationException) {
            throw ConflictException(ConflictErrorCode.DUPLICATE_EMAIL, e)
        }

    override fun findUser(userId: Long): User =
        userRepository.findByIdAndDeletedAtIsNull(userId)
            ?: throw NotFoundException(NotFoundErrorCode.USER_NOT_FOUND)

    override fun getUserByEmail(email: String): User? =
        userRepository.findByEmailAndDeletedAtIsNull(email)

    override fun existsByEmail(email: String): Boolean =
        userRepository.existsByEmail(email)

}
