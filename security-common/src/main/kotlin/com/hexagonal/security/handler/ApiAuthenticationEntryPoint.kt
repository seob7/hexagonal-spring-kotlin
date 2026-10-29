package com.hexagonal.security.handler

import com.hexagonal.common.exception.UnauthorizedException
import com.hexagonal.common.exception.errorcode.UnauthorizedErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver

/**
 * 인증 실패(401)를 공통 응답 규격으로 내려준다.
 *
 * 필터 단계의 예외는 `@RestControllerAdvice` 에 닿지 않으므로, [HandlerExceptionResolver] 로 넘겨
 * common 의 `UnauthorizedExceptionHandler` 가 컨트롤러 예외와 똑같이 `Api.ERROR` 를 만들게 한다.
 */
@Component
class ApiAuthenticationEntryPoint(
    @param:Qualifier("handlerExceptionResolver") private val handlerExceptionResolver: HandlerExceptionResolver,
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        // 토큰이 있는데 위조·만료된 경우와 토큰이 아예 없는 경우를 구분한다. (클라이언트의 재발급 판단용)
        val errorCode = when (authException) {
            is InvalidBearerTokenException -> UnauthorizedErrorCode.INVALID_TOKEN
            else -> UnauthorizedErrorCode.UNAUTHORIZED
        }
        handlerExceptionResolver.resolveException(
            request, response, null, UnauthorizedException(errorCode, authException)
        )
    }

}
