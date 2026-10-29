package com.hexagonal.user.fake

import com.hexagonal.user.application.port.out.RefreshTokenPersistencePort
import com.hexagonal.user.domain.RefreshToken

class FakeRefreshTokenPersistencePort : RefreshTokenPersistencePort {

    private val refreshTokens = mutableMapOf<String, RefreshToken>()

    override fun save(refreshToken: RefreshToken) {
        refreshTokens[refreshToken.token] = refreshToken
    }

    override fun getRefreshToken(token: String): RefreshToken? = refreshTokens[token]

    override fun delete(refreshToken: RefreshToken): Boolean =
        refreshTokens.remove(refreshToken.token) != null

    override fun deleteAllByUserId(userId: Long) {
        refreshTokens.values.removeIf { it.userId == userId }
    }

    fun countByUserId(userId: Long): Int = refreshTokens.values.count { it.userId == userId }

}
