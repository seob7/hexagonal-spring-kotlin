package com.hexagonal.user.application.port.`in`.dto

data class LoginCommand(
    val email: String,
    val password: String,
)
