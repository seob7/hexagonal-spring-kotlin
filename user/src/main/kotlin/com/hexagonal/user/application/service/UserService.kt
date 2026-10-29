package com.hexagonal.user.application.service

import com.hexagonal.common.exception.ConflictException
import com.hexagonal.common.exception.ValidationException
import com.hexagonal.common.exception.errorcode.ConflictErrorCode
import com.hexagonal.common.exception.errorcode.ValidationErrorCode
import com.hexagonal.user.application.port.`in`.UserCommandUseCase
import com.hexagonal.user.application.port.`in`.UserQueryUseCase
import com.hexagonal.user.application.port.`in`.dto.ChangePasswordCommand
import com.hexagonal.user.application.port.`in`.dto.SignUpCommand
import com.hexagonal.user.application.port.out.PasswordEncoderPort
import com.hexagonal.user.application.port.out.RefreshTokenPersistencePort
import com.hexagonal.user.application.port.out.UserPersistencePort
import com.hexagonal.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userPersistencePort: UserPersistencePort,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val passwordEncoderPort: PasswordEncoderPort,
) : UserCommandUseCase, UserQueryUseCase {

    @Transactional
    override fun signUp(command: SignUpCommand): User {
        if (userPersistencePort.existsByEmail(command.email)) {
            throw ConflictException(ConflictErrorCode.DUPLICATE_EMAIL)
        }
        return userPersistencePort.create(
            User(
                email = command.email,
                password = passwordEncoderPort.encode(command.password),
                nickname = command.nickname,
            )
        )
    }

    override fun findUser(userId: Long): User =
        userPersistencePort.findUser(userId)

    @Transactional
    override fun changePassword(userId: Long, command: ChangePasswordCommand) {
        val user = userPersistencePort.findUser(userId)
        if (!passwordEncoderPort.matches(command.currentPassword, user.password)) {
            throw ValidationException(ValidationErrorCode.CURRENT_PASSWORD_MISMATCH)
        }
        user.changePassword(passwordEncoderPort.encode(command.newPassword))
        // 비밀번호가 바뀌면 다른 기기의 로그인도 모두 끊는다.
        refreshTokenPersistencePort.deleteAllByUserId(userId)
    }

    @Transactional
    override fun withdraw(userId: Long) {
        userPersistencePort.findUser(userId).withdraw()
        refreshTokenPersistencePort.deleteAllByUserId(userId)
    }

}
