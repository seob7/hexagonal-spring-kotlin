package com.hexagonal.security

/**
 * 인증된 요청의 사용자. Access Token(JWT) 클레임에서 복원한다.
 *
 * 인바운드 어댑터 메서드의 파라미터로 선언하면
 * [com.hexagonal.security.resolver.AuthUserArgumentResolver] 가 주입한다.
 * 어댑터가 Spring Security 타입(`Authentication`, `Jwt`)을 몰라도 되게 하는 경계 모델이다.
 *
 * ```kotlin
 * @GetMapping("/me")
 * fun findMe(authUser: AuthUser): ResponseEntity<Api<UserResponse>> = ...
 * ```
 */
data class AuthUser(
    val userId: Long,
    val role: String,
)
