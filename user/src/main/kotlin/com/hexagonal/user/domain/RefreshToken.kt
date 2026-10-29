package com.hexagonal.user.domain

import java.time.Instant

/**
 * Refresh Token. JPA 엔티티가 아닌 **순수 도메인 모델**이다.
 *
 * [User] 와 달리 저장 형태(Redis 키·TTL·해시)를 모른다. 매핑은 영속성 어댑터의 몫이고,
 * 더티 체킹이 없으므로 저장·삭제는 포트를 명시적으로 호출한다. (루트 README 4.4 #1·#2 의 정통 방식)
 */
class RefreshToken(
    val userId: Long,
    val token: String,
    val expiresAt: Instant,
) {

    fun isOwnedBy(userId: Long): Boolean = this.userId == userId

}
