package com.hexagonal.user.adapter.out.persistence

import com.hexagonal.common.annotation.PersistenceAdapter
import com.hexagonal.user.application.port.out.RefreshTokenPersistencePort
import com.hexagonal.user.domain.RefreshToken
import org.springframework.data.redis.core.StringRedisTemplate
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.util.HexFormat

/**
 * Refresh Token 을 Redis 에 저장한다. 도메인 [RefreshToken] 과 저장 형태가 다르므로 여기서 매핑한다.
 *
 * - `refresh-token:{sha256(token)}` → `"{userId}:{expiresAt epoch seconds}"`, TTL = 남은 유효기간
 * - `refresh-token:user:{userId}` → 그 사용자의 토큰 해시 Set (전체 로그아웃용)
 *
 * 원문 토큰은 저장하지 않는다. 저장소가 유출돼도 해시로는 재발급을 요청할 수 없다.
 */
@PersistenceAdapter
class RefreshTokenPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
) : RefreshTokenPersistencePort {

    override fun save(refreshToken: RefreshToken) {
        val tokenHash = hash(refreshToken.token)
        val ttl = Duration.between(Instant.now(), refreshToken.expiresAt)

        redisTemplate.opsForValue().set(
            tokenKey(tokenHash),
            "${refreshToken.userId}$VALUE_DELIMITER${refreshToken.expiresAt.epochSecond}",
            ttl,
        )
        redisTemplate.opsForSet().add(userKey(refreshToken.userId), tokenHash)
        // 사용자 Set 은 가장 마지막에 발급된(= 가장 늦게 만료되는) 토큰과 함께 만료된다.
        redisTemplate.expire(userKey(refreshToken.userId), ttl)
    }

    override fun getRefreshToken(token: String): RefreshToken? {
        val value = redisTemplate.opsForValue().get(tokenKey(hash(token))) ?: return null
        val (userId, expiresAt) = value.split(VALUE_DELIMITER)
        return RefreshToken(userId.toLong(), token, Instant.ofEpochSecond(expiresAt.toLong()))
    }

    override fun delete(refreshToken: RefreshToken): Boolean {
        val tokenHash = hash(refreshToken.token)
        redisTemplate.opsForSet().remove(userKey(refreshToken.userId), tokenHash)
        return redisTemplate.delete(tokenKey(tokenHash))
    }

    override fun deleteAllByUserId(userId: Long) {
        val tokenHashes = redisTemplate.opsForSet().members(userKey(userId)).orEmpty()
        redisTemplate.delete(tokenHashes.map(::tokenKey) + userKey(userId))
    }

    private fun hash(token: String): String =
        HexFormat.of().formatHex(MessageDigest.getInstance(HASH_ALGORITHM).digest(token.toByteArray()))

    private fun tokenKey(tokenHash: String): String = "$KEY_PREFIX:$tokenHash"

    private fun userKey(userId: Long): String = "$KEY_PREFIX:user:$userId"

    companion object {
        private const val KEY_PREFIX = "refresh-token"
        private const val VALUE_DELIMITER = ":"
        private const val HASH_ALGORITHM = "SHA-256"
    }

}
