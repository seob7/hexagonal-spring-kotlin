package com.hexagonal.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.security.converter.RsaKeyConverters
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import java.security.interfaces.RSAPublicKey

/**
 * Access Token 검증기. RS256 공개키로 서명과 만료(`exp`)를 검증한다.
 *
 * 공개키만 있으면 검증할 수 있으므로, 어느 실행 모듈이든 user 모듈을 호출하지 않고 스스로 토큰을 검증한다.
 * 개인키가 없으니 토큰을 발급(위조)할 수는 없다.
 */
@Configuration
class JwtDecoderConfig {

    /** user 모듈의 JwtEncoder 도 이 공개키 빈을 함께 쓴다. */
    @Bean
    fun jwtPublicKey(
        @Value("\${hexagonal.jwt.public-key-location}") publicKeyLocation: Resource,
    ): RSAPublicKey =
        publicKeyLocation.inputStream.use { requireNotNull(RsaKeyConverters.x509().convert(it)) }

    @Bean
    fun jwtDecoder(jwtPublicKey: RSAPublicKey): JwtDecoder =
        NimbusJwtDecoder.withPublicKey(jwtPublicKey).build()

}
