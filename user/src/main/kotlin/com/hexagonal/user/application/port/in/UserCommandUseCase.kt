package com.hexagonal.user.application.port.`in`

import com.hexagonal.user.application.port.`in`.dto.ChangePasswordCommand
import com.hexagonal.user.application.port.`in`.dto.SignUpCommand
import com.hexagonal.user.domain.User

interface UserCommandUseCase {

    fun signUp(command: SignUpCommand): User

    fun changePassword(userId: Long, command: ChangePasswordCommand)

    fun withdraw(userId: Long)

}
