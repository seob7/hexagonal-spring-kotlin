package com.hexagonal.user.application.port.`in`.dto

import java.time.Instant

/**
 * 토큰 발급 유스케이스의 결과 모델.
 * 토큰은 엔티티가 아니므로, 도메인 객체 대신 이 결과 모델을 인바운드 어댑터로 돌려준다.
 */
data class TokenResult(
    val accessToken: String,
    val accessTokenExpiresAt: Instant,
    val refreshToken: String,
    val refreshTokenExpiresAt: Instant,
)
