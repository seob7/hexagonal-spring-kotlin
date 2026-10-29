package com.hexagonal.user.application.port.`in`.dto

data class SignUpCommand(
    val email: String,
    val password: String,
    val nickname: String,
)
