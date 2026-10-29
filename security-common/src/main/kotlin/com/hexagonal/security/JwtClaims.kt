package com.hexagonal.security

/**
 * 토큰을 발급하는 쪽(user 모듈)과 검증하는 쪽(security-common)이 공유하는 JWT 클레임 계약.
 * 사용자 식별자는 표준 클레임 `sub` 에 담는다.
 */
object JwtClaims {

    /** 권한 클레임. 값은 `USER`, `ADMIN` 처럼 `ROLE_` 접두사 없는 역할명이다. */
    const val ROLE = "role"

}
