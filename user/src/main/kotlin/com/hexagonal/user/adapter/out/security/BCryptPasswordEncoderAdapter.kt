package com.hexagonal.user.adapter.out.security

import com.hexagonal.common.annotation.OutputAdapter
import com.hexagonal.user.application.port.out.PasswordEncoderPort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@OutputAdapter
class BCryptPasswordEncoderAdapter : PasswordEncoderPort {

    private val passwordEncoder = BCryptPasswordEncoder()

    override fun encode(rawPassword: String): String =
        requireNotNull(passwordEncoder.encode(rawPassword))

    override fun matches(rawPassword: String, encodedPassword: String): Boolean =
        passwordEncoder.matches(rawPassword, encodedPassword)

}
