package com.hexagonal.user.application.port.out

import com.hexagonal.user.domain.AccessToken
import com.hexagonal.user.domain.RefreshToken
import com.hexagonal.user.domain.UserRole

interface TokenIssuerPort {

    fun issueAccessToken(userId: Long, role: UserRole): AccessToken

    fun issueRefreshToken(userId: Long): RefreshToken

}
