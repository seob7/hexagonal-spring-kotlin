package com.hexagonal.user.application.service

import com.hexagonal.common.exception.UnauthorizedException
import com.hexagonal.common.exception.errorcode.UnauthorizedErrorCode
import com.hexagonal.user.application.port.`in`.AuthCommandUseCase
import com.hexagonal.user.application.port.`in`.dto.LoginCommand
import com.hexagonal.user.application.port.`in`.dto.LogoutCommand
import com.hexagonal.user.application.port.`in`.dto.ReissueCommand
import com.hexagonal.user.application.port.`in`.dto.TokenResult
import com.hexagonal.user.application.port.out.PasswordEncoderPort
import com.hexagonal.user.application.port.out.RefreshTokenPersistencePort
import com.hexagonal.user.application.port.out.TokenIssuerPort
import com.hexagonal.user.application.port.out.UserPersistencePort
import com.hexagonal.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 로그인 · 토큰 재발급 · 로그아웃.
 *
 * JPA 쓰기가 없으므로 클래스 기본값(readOnly)만 둔다.
 * Refresh Token 은 Redis 에 저장되며 JPA 트랜잭션과 무관하게 즉시 반영된다.
 */
@Service
@Transactional(readOnly = true)
class AuthService(
    private val userPersistencePort: UserPersistencePort,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val passwordEncoderPort: PasswordEncoderPort,
    private val tokenIssuerPort: TokenIssuerPort,
) : AuthCommandUseCase {

    override fun login(command: LoginCommand): TokenResult {
        // 이메일 없음과 비밀번호 불일치를 같은 에러로 응답해, 가입 여부가 드러나지 않게 한다.
        val user = userPersistencePort.getUserByEmail(command.email)
            ?.takeIf { passwordEncoderPort.matches(command.password, it.password) }
            ?: throw UnauthorizedException(UnauthorizedErrorCode.INVALID_CREDENTIALS)

        return issueTokens(user)
    }

    override fun reissue(command: ReissueCommand): TokenResult {
        // Refresh Token Rotation: 사용한 토큰은 즉시 폐기하고 새로 발급한다.
        // delete 가 false 면 동시 요청이 먼저 폐기한 것이므로, 같은 토큰으로 두 번 재발급되지 않는다.
        val refreshToken = refreshTokenPersistencePort.getRefreshToken(command.refreshToken)
            ?.takeIf { refreshTokenPersistencePort.delete(it) }
            ?: throw UnauthorizedException(UnauthorizedErrorCode.INVALID_REFRESH_TOKEN)

        return issueTokens(userPersistencePort.findUser(refreshToken.userId))
    }

    override fun logout(userId: Long, command: LogoutCommand) {
        // 이미 만료·폐기된 토큰이어도 성공으로 본다. (멱등)
        refreshTokenPersistencePort.getRefreshToken(command.refreshToken)
            ?.takeIf { it.isOwnedBy(userId) }
            ?.let { refreshTokenPersistencePort.delete(it) }
    }

    private fun issueTokens(user: User): TokenResult {
        val userId = requireNotNull(user.id)
        val accessToken = tokenIssuerPort.issueAccessToken(userId, user.role)
        val refreshToken = tokenIssuerPort.issueRefreshToken(userId)
        refreshTokenPersistencePort.save(refreshToken)

        return TokenResult(
            accessToken = accessToken.token,
            accessTokenExpiresAt = accessToken.expiresAt,
            refreshToken = refreshToken.token,
            refreshTokenExpiresAt = refreshToken.expiresAt,
        )
    }

}
