package com.hexagonal.user.domain

import java.time.Instant

/**
 * Access Token. 서명된 JWT 문자열과 만료 시각만 가진다.
 * 서버에 저장하지 않으며, 검증은 각 모듈이 공개키로 한다.
 */
class AccessToken(
    val token: String,
    val expiresAt: Instant,
)
