package com.hexagonal.user.adapter.`in`

import com.hexagonal.common.annotation.WebInputAdapter
import com.hexagonal.security.AuthUser
import com.hexagonal.user.adapter.`in`.dto.LogoutRequest
import com.hexagonal.user.application.port.`in`.AuthCommandUseCase
import com.hexagonal.user.config.OpenApiConfig
import io.github.seob7.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * 로그인한 사용자의 토큰 API. (/api — Access Token 필요)
 */
@Tag(name = "Auth")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
@WebInputAdapter("/api/auth")
class AuthApiAdapter(
    private val authCommandUseCase: AuthCommandUseCase,
) {

    @Operation(
        summary = "로그아웃 (토큰 폐기)",
        description = "이 기기의 Refresh Token 을 폐기한다. Access Token 은 만료 시각까지 유효하므로 클라이언트에서 삭제한다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "폐기 성공 (이미 폐기된 토큰이어도 성공)"),
        ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @DeleteMapping("/token")
    fun logout(
        authUser: AuthUser,
        @RequestBody @Valid request: LogoutRequest,
    ): ResponseEntity<Api<Boolean>> {
        authCommandUseCase.logout(authUser.userId, request.toCommand())
        return Api.OK(true)
    }

}
