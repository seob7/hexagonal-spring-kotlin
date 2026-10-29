package com.hexagonal.user.application.port.`in`

import com.hexagonal.user.domain.User

interface UserQueryUseCase {

    fun findUser(userId: Long): User

}
