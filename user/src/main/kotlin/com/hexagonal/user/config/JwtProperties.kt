package com.hexagonal.user.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource
import java.time.Duration

/**
 * 토큰 발급 설정. (`application.yaml` 의 `hexagonal.jwt`)
 * 검증용 공개키(`hexagonal.jwt.public-key-location`)는 security-common 이 읽는다.
 */
@ConfigurationProperties(prefix = "hexagonal.jwt")
data class JwtProperties(
    val privateKeyLocation: Resource,
    val accessTokenTtl: Duration,
    val refreshTokenTtl: Duration,
)
