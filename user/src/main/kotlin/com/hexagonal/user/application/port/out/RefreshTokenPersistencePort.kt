package com.hexagonal.user.application.port.out

import com.hexagonal.user.domain.RefreshToken

/**
 * Refresh Token 저장소 계약.
 *
 * [UserPersistencePort] 와 달리 저장·삭제를 명시적으로 호출한다.
 * 구현이 Redis 라 더티 체킹이 없기 때문이다. (루트 README 4.4 #1 참고)
 */
interface RefreshTokenPersistencePort {

    fun save(refreshToken: RefreshToken)

    /** 없거나 만료된 토큰이면 null. */
    fun getRefreshToken(token: String): RefreshToken?

    /**
     * @return 이 호출이 실제로 삭제했으면 true.
     * 같은 토큰을 동시에 삭제하면 한 쪽만 true 를 받으므로, 재발급 중복을 막는 데 쓴다.
     */
    fun delete(refreshToken: RefreshToken): Boolean

    fun deleteAllByUserId(userId: Long)

}
