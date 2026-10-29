package com.hexagonal.user.fake

import com.hexagonal.user.application.port.out.TokenIssuerPort
import com.hexagonal.user.domain.AccessToken
import com.hexagonal.user.domain.RefreshToken
import com.hexagonal.user.domain.UserRole
import java.time.Duration
import java.time.Instant

class FakeTokenIssuerPort : TokenIssuerPort {

    private var sequence = 0

    override fun issueAccessToken(userId: Long, role: UserRole): AccessToken =
        AccessToken("access-$userId-${++sequence}", Instant.now().plus(Duration.ofMinutes(30)))

    override fun issueRefreshToken(userId: Long): RefreshToken =
        RefreshToken(userId, "refresh-$userId-${++sequence}", Instant.now().plus(Duration.ofDays(14)))

}
