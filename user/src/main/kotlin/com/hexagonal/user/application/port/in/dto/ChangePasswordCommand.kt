package com.hexagonal.user.application.port.`in`.dto

data class ChangePasswordCommand(
    val currentPassword: String,
    val newPassword: String,
)
