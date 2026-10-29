package com.hexagonal.user.application.service

import com.hexagonal.common.exception.ConflictException
import com.hexagonal.common.exception.NotFoundException
import com.hexagonal.common.exception.ValidationException
import com.hexagonal.common.exception.errorcode.ConflictErrorCode
import com.hexagonal.common.exception.errorcode.ValidationErrorCode
import com.hexagonal.user.application.port.`in`.dto.ChangePasswordCommand
import com.hexagonal.user.application.port.`in`.dto.SignUpCommand
import com.hexagonal.user.domain.RefreshToken
import com.hexagonal.user.fake.FakePasswordEncoderPort
import com.hexagonal.user.fake.FakeRefreshTokenPersistencePort
import com.hexagonal.user.fake.FakeUserPersistencePort
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class UserServiceTest {

    private lateinit var userPersistencePort: FakeUserPersistencePort
    private lateinit var refreshTokenPersistencePort: FakeRefreshTokenPersistencePort
    private lateinit var passwordEncoderPort: FakePasswordEncoderPort
    private lateinit var userService: UserService

    @BeforeTest
    fun setUp() {
        userPersistencePort = FakeUserPersistencePort()
        refreshTokenPersistencePort = FakeRefreshTokenPersistencePort()
        passwordEncoderPort = FakePasswordEncoderPort()
        userService = UserService(userPersistencePort, refreshTokenPersistencePort, passwordEncoderPort)
    }

    @Test
    fun `회원가입하면 비밀번호를 인코딩해서 저장한다`() {
        val user = userService.signUp(SignUpCommand(EMAIL, PASSWORD, "tester"))

        assertEquals(passwordEncoderPort.encode(PASSWORD), user.password)
    }

    @Test
    fun `이미 가입된 이메일이면 DUPLICATE_EMAIL`() {
        userService.signUp(SignUpCommand(EMAIL, PASSWORD, "tester"))

        val e = assertFailsWith<ConflictException> {
            userService.signUp(SignUpCommand(EMAIL, PASSWORD, "another"))
        }
        assertEquals(ConflictErrorCode.DUPLICATE_EMAIL, e.baseErrorCode)
    }

    @Test
    fun `현재 비밀번호가 틀리면 CURRENT_PASSWORD_MISMATCH`() {
        val userId = signUp()

        val e = assertFailsWith<ValidationException> {
            userService.changePassword(userId, ChangePasswordCommand("wrong-password", NEW_PASSWORD))
        }
        assertEquals(ValidationErrorCode.CURRENT_PASSWORD_MISMATCH, e.baseErrorCode)
    }

    @Test
    fun `비밀번호를 바꾸면 모든 기기의 Refresh Token 이 폐기된다`() {
        val userId = signUp()
        saveRefreshTokens(userId, count = 2)

        userService.changePassword(userId, ChangePasswordCommand(PASSWORD, NEW_PASSWORD))

        assertEquals(passwordEncoderPort.encode(NEW_PASSWORD), userService.findUser(userId).password)
        assertEquals(0, refreshTokenPersistencePort.countByUserId(userId))
    }

    @Test
    fun `탈퇴하면 조회되지 않고 Refresh Token 이 모두 폐기된다`() {
        val userId = signUp()
        saveRefreshTokens(userId, count = 2)

        userService.withdraw(userId)

        assertFailsWith<NotFoundException> { userService.findUser(userId) }
        assertEquals(0, refreshTokenPersistencePort.countByUserId(userId))
    }

    @Test
    fun `탈퇴한 회원의 이메일로 다시 가입할 수 있다`() {
        val userId = signUp()
        userService.withdraw(userId)

        val rejoined = userService.signUp(SignUpCommand(EMAIL, PASSWORD, "rejoined"))

        assertNotEquals(userId, assertNotNull(rejoined.id))
        assertEquals(EMAIL, rejoined.email)
    }

    private fun signUp(): Long =
        requireNotNull(userService.signUp(SignUpCommand(EMAIL, PASSWORD, "tester")).id)

    private fun saveRefreshTokens(userId: Long, count: Int) =
        repeat(count) {
            refreshTokenPersistencePort.save(RefreshToken(userId, "refresh-$it", Instant.now().plusSeconds(60)))
        }

    companion object {
        private const val EMAIL = "user@hexagonal.com"
        private const val PASSWORD = "password1234"
        private const val NEW_PASSWORD = "new-password1234"
    }

}
