package com.hexagonal.user.adapter.out.token

import com.hexagonal.common.annotation.OutputAdapter
import com.hexagonal.security.JwtClaims
import com.hexagonal.user.application.port.out.TokenIssuerPort
import com.hexagonal.user.config.JwtProperties
import com.hexagonal.user.domain.AccessToken
import com.hexagonal.user.domain.RefreshToken
import com.hexagonal.user.domain.UserRole
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64

@OutputAdapter
class TokenIssuerAdapter(
    private val jwtEncoder: JwtEncoder,
    private val jwtProperties: JwtProperties,
) : TokenIssuerPort {

    private val secureRandom = SecureRandom()

    /** RS256 으로 서명한 JWT. `sub` = userId, `role` = 권한. */
    override fun issueAccessToken(userId: Long, role: UserRole): AccessToken {
        val issuedAt = Instant.now()
        val expiresAt = issuedAt.plus(jwtProperties.accessTokenTtl)
        val claims = JwtClaimsSet.builder()
            .subject(userId.toString())
            .claim(JwtClaims.ROLE, role.name)
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .build()

        return AccessToken(jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue, expiresAt)
    }

    /** JWT 가 아닌 불투명(opaque) 난수. 서명이 아니라 저장소 조회로 검증한다. */
    override fun issueRefreshToken(userId: Long): RefreshToken {
        val bytes = ByteArray(REFRESH_TOKEN_BYTES).also(secureRandom::nextBytes)
        return RefreshToken(
            userId = userId,
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes),
            expiresAt = Instant.now().plus(jwtProperties.refreshTokenTtl),
        )
    }

    companion object {
        private const val REFRESH_TOKEN_BYTES = 32
    }

}
