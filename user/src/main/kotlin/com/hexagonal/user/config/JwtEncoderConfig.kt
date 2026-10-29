package com.hexagonal.user.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.converter.RsaKeyConverters
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.security.interfaces.RSAPublicKey

/**
 * Access Token 서명기. 개인키는 토큰을 발급하는 이 모듈만 가진다.
 * 공개키는 security-common 의 `jwtPublicKey` 빈을 그대로 써서, 발급·검증 키 쌍이 어긋나지 않게 한다.
 */
@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtEncoderConfig {

    @Bean
    fun jwtEncoder(jwtPublicKey: RSAPublicKey, jwtProperties: JwtProperties): JwtEncoder {
        val privateKey = jwtProperties.privateKeyLocation.inputStream.use {
            requireNotNull(RsaKeyConverters.pkcs8().convert(it))
        }
        return NimbusJwtEncoder.withKeyPair(jwtPublicKey, privateKey).build()
    }

}
