package com.hexagonal.security.config

import com.hexagonal.security.JwtClaims
import com.hexagonal.security.handler.ApiAccessDeniedHandler
import com.hexagonal.security.handler.ApiAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

/**
 * 모든 실행 모듈이 공유하는 보안 필터 체인.
 *
 * - `/open-api` 하위와 API 문서는 인증 없이 허용하고, 그 외(`/api` 하위 포함)는 Access Token 을 요구한다.
 * - 세션을 쓰지 않는다(STATELESS). 요청마다 `Authorization: Bearer <JWT>` 를 검증한다.
 * - 토큰은 검증만 한다. 발급은 user 모듈의 책임이다.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val apiAuthenticationEntryPoint: ApiAuthenticationEntryPoint,
    private val apiAccessDeniedHandler: ApiAccessDeniedHandler,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            httpBasic { disable() }
            formLogin { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeHttpRequests {
                PERMIT_ALL_PATTERNS.forEach { authorize(it, permitAll) }
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = jwtAuthenticationConverter()
                }
                // 토큰이 있지만 유효하지 않을 때
                authenticationEntryPoint = apiAuthenticationEntryPoint
                accessDeniedHandler = apiAccessDeniedHandler
            }
            exceptionHandling {
                // 토큰 없이 보호된 경로에 접근했을 때
                authenticationEntryPoint = apiAuthenticationEntryPoint
                accessDeniedHandler = apiAccessDeniedHandler
            }
        }
        return http.build()
    }

    /** `role` 클레임(`USER`)을 Spring Security 권한(`ROLE_USER`)으로 변환한다. */
    private fun jwtAuthenticationConverter(): JwtAuthenticationConverter =
        JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(
                JwtGrantedAuthoritiesConverter().apply {
                    setAuthoritiesClaimName(JwtClaims.ROLE)
                    setAuthorityPrefix(ROLE_PREFIX)
                }
            )
        }

    companion object {
        private const val ROLE_PREFIX = "ROLE_"

        private val PERMIT_ALL_PATTERNS = listOf(
            "/open-api/**",
            "/scalar/**",
            "/v3/api-docs/**",
            "/error",
        )
    }

}
