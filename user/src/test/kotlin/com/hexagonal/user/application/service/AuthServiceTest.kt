package com.hexagonal.user.application.service

import com.hexagonal.common.exception.UnauthorizedException
import com.hexagonal.common.exception.errorcode.UnauthorizedErrorCode
import com.hexagonal.user.application.port.`in`.dto.LoginCommand
import com.hexagonal.user.application.port.`in`.dto.LogoutCommand
import com.hexagonal.user.application.port.`in`.dto.ReissueCommand
import com.hexagonal.user.domain.User
import com.hexagonal.user.fake.FakePasswordEncoderPort
import com.hexagonal.user.fake.FakeRefreshTokenPersistencePort
import com.hexagonal.user.fake.FakeTokenIssuerPort
import com.hexagonal.user.fake.FakeUserPersistencePort
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthServiceTest {

    private lateinit var userPersistencePort: FakeUserPersistencePort
    private lateinit var refreshTokenPersistencePort: FakeRefreshTokenPersistencePort
    private lateinit var authService: AuthService
    private lateinit var user: User

    @BeforeTest
    fun setUp() {
        userPersistencePort = FakeUserPersistencePort()
        refreshTokenPersistencePort = FakeRefreshTokenPersistencePort()
        val passwordEncoderPort = FakePasswordEncoderPort()
        authService = AuthService(
            userPersistencePort,
            refreshTokenPersistencePort,
            passwordEncoderPort,
            FakeTokenIssuerPort(),
        )
        user = userPersistencePort.create(
            User(EMAIL, passwordEncoderPort.encode(PASSWORD), "tester")
        )
    }

    @Test
    fun `로그인하면 토큰 쌍을 발급하고 Refresh Token 을 저장한다`() {
        val result = authService.login(LoginCommand(EMAIL, PASSWORD))

        val saved = assertNotNull(refreshTokenPersistencePort.getRefreshToken(result.refreshToken))
        assertEquals(user.id, saved.userId)
    }

    @Test
    fun `비밀번호가 틀리면 INVALID_CREDENTIALS`() {
        val e = assertFailsWith<UnauthorizedException> {
            authService.login(LoginCommand(EMAIL, "wrong-password"))
        }
        assertEquals(UnauthorizedErrorCode.INVALID_CREDENTIALS, e.baseErrorCode)
    }

    @Test
    fun `없는 이메일도 비밀번호 불일치와 같은 에러로 응답한다`() {
        val e = assertFailsWith<UnauthorizedException> {
            authService.login(LoginCommand("nobody@hexagonal.com", PASSWORD))
        }
        assertEquals(UnauthorizedErrorCode.INVALID_CREDENTIALS, e.baseErrorCode)
    }

    @Test
    fun `재발급하면 사용한 Refresh Token 은 폐기되고 새 토큰이 저장된다`() {
        val issued = authService.login(LoginCommand(EMAIL, PASSWORD))

        val reissued = authService.reissue(ReissueCommand(issued.refreshToken))

        assertNotEquals(issued.refreshToken, reissued.refreshToken)
        assertNull(refreshTokenPersistencePort.getRefreshToken(issued.refreshToken))
        assertNotNull(refreshTokenPersistencePort.getRefreshToken(reissued.refreshToken))
    }

    @Test
    fun `이미 사용한 Refresh Token 으로 재발급하면 INVALID_REFRESH_TOKEN`() {
        val issued = authService.login(LoginCommand(EMAIL, PASSWORD))
        authService.reissue(ReissueCommand(issued.refreshToken))

        val e = assertFailsWith<UnauthorizedException> {
            authService.reissue(ReissueCommand(issued.refreshToken))
        }
        assertEquals(UnauthorizedErrorCode.INVALID_REFRESH_TOKEN, e.baseErrorCode)
    }

    @Test
    fun `로그아웃하면 해당 Refresh Token 만 폐기한다`() {
        val thisDevice = authService.login(LoginCommand(EMAIL, PASSWORD))
        val otherDevice = authService.login(LoginCommand(EMAIL, PASSWORD))

        authService.logout(requireNotNull(user.id), LogoutCommand(thisDevice.refreshToken))

        assertNull(refreshTokenPersistencePort.getRefreshToken(thisDevice.refreshToken))
        assertNotNull(refreshTokenPersistencePort.getRefreshToken(otherDevice.refreshToken))
    }

    @Test
    fun `다른 사용자의 Refresh Token 으로는 로그아웃시킬 수 없다`() {
        val issued = authService.login(LoginCommand(EMAIL, PASSWORD))

        authService.logout(OTHER_USER_ID, LogoutCommand(issued.refreshToken))

        assertNotNull(refreshTokenPersistencePort.getRefreshToken(issued.refreshToken))
    }

    companion object {
        private const val EMAIL = "user@hexagonal.com"
        private const val PASSWORD = "password1234"
        private const val OTHER_USER_ID = 999L
    }

}
