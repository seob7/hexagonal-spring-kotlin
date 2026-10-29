package com.hexagonal.user.application.port.`in`

import com.hexagonal.user.application.port.`in`.dto.LoginCommand
import com.hexagonal.user.application.port.`in`.dto.LogoutCommand
import com.hexagonal.user.application.port.`in`.dto.ReissueCommand
import com.hexagonal.user.application.port.`in`.dto.TokenResult

interface AuthCommandUseCase {

    fun login(command: LoginCommand): TokenResult

    fun reissue(command: ReissueCommand): TokenResult

    fun logout(userId: Long, command: LogoutCommand)

}
