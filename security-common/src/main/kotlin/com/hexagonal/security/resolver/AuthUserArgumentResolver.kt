package com.hexagonal.security.resolver

import com.hexagonal.common.exception.UnauthorizedException
import com.hexagonal.common.exception.errorcode.UnauthorizedErrorCode
import com.hexagonal.security.AuthUser
import com.hexagonal.security.JwtClaims
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * 컨트롤러 파라미터 타입이 [AuthUser] 이면, 검증을 마친 JWT 에서 사용자 정보를 꺼내 주입한다.
 */
class AuthUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == AuthUser::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): AuthUser {
        // /open-api 처럼 인증이 없는 경로에서 AuthUser 를 선언한 경우
        val authentication = SecurityContextHolder.getContext().authentication as? JwtAuthenticationToken
            ?: throw UnauthorizedException(UnauthorizedErrorCode.UNAUTHORIZED)

        val jwt = authentication.token
        return AuthUser(
            userId = requireNotNull(jwt.subject).toLong(),
            role = requireNotNull(jwt.getClaimAsString(JwtClaims.ROLE)),
        )
    }

}
