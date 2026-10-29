package com.hexagonal.user.fake

import com.hexagonal.user.application.port.out.PasswordEncoderPort

/** 해시 대신 접두사만 붙인다. "인코딩된 값이 저장되는가" 만 검증하면 충분하다. */
class FakePasswordEncoderPort : PasswordEncoderPort {

    override fun encode(rawPassword: String): String = "$PREFIX$rawPassword"

    override fun matches(rawPassword: String, encodedPassword: String): Boolean =
        encodedPassword == encode(rawPassword)

    companion object {
        const val PREFIX = "encoded:"
    }

}
