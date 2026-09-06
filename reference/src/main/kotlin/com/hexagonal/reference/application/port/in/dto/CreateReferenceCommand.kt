package com.hexagonal.reference.application.port.`in`.dto

data class CreateReferenceCommand(
    val title: String,
    val content: String,
)
