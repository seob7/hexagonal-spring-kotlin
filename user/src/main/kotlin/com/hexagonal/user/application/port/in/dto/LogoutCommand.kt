package com.hexagonal.user.application.port.`in`.dto

data class LogoutCommand(
    val refreshToken: String,
)
