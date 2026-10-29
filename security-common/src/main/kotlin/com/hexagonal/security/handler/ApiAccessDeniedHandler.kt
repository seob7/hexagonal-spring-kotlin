package com.hexagonal.security.handler

import com.hexagonal.common.exception.ForbiddenException
import com.hexagonal.common.exception.errorcode.ForbiddenErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver

/**
 * 인가 실패(403)를 공통 응답 규격으로 내려준다.
 * 동작 방식은 [ApiAuthenticationEntryPoint] 와 같다.
 */
@Component
class ApiAccessDeniedHandler(
    @param:Qualifier("handlerExceptionResolver") private val handlerExceptionResolver: HandlerExceptionResolver,
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        handlerExceptionResolver.resolveException(
            request, response, null, ForbiddenException(ForbiddenErrorCode.FORBIDDEN, accessDeniedException)
        )
    }

}
