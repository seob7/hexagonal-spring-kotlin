package com.hexagonal.user.adapter.`in`.dto

import com.hexagonal.user.application.port.`in`.dto.TokenResult
import java.time.Instant

data class TokenResponse(
    val tokenType: String,
    val accessToken: String,
    val accessTokenExpiresAt: Instant,
    val refreshToken: String,
    val refreshTokenExpiresAt: Instant,
) {

    companion object {

        private const val BEARER = "Bearer"

        fun from(result: TokenResult): TokenResponse =
            TokenResponse(
                tokenType = BEARER,
                accessToken = result.accessToken,
                accessTokenExpiresAt = result.accessTokenExpiresAt,
                refreshToken = result.refreshToken,
                refreshTokenExpiresAt = result.refreshTokenExpiresAt,
            )
    }
}
