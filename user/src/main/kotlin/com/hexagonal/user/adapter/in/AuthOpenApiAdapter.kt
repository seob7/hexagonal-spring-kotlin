package com.hexagonal.user.adapter.`in`

import com.hexagonal.common.annotation.WebInputAdapter
import com.hexagonal.user.adapter.`in`.dto.LoginRequest
import com.hexagonal.user.adapter.`in`.dto.ReissueRequest
import com.hexagonal.user.adapter.`in`.dto.TokenResponse
import com.hexagonal.user.application.port.`in`.AuthCommandUseCase
import io.github.seob7.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * 인증 없이 호출하는 토큰 API. (/open-api)
 * URL 에 동사(`/login`)를 쓰지 않도록 토큰을 자원으로 다룬다: 발급 = POST, 교체 = PUT, 폐기 = DELETE.
 */
@Tag(name = "Auth", description = "인증 토큰 API")
@WebInputAdapter("/open-api/auth")
class AuthOpenApiAdapter(
    private val authCommandUseCase: AuthCommandUseCase,
) {

    @Operation(summary = "로그인 (토큰 발급)", description = "이메일·비밀번호로 Access Token 과 Refresh Token 을 발급한다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "발급 성공"),
        ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
        ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치"),
    )
    @PostMapping("/token")
    fun login(
        @RequestBody @Valid request: LoginRequest,
    ): ResponseEntity<Api<TokenResponse>> =
        Api.OK(TokenResponse.from(authCommandUseCase.login(request.toCommand())))

    @Operation(
        summary = "토큰 재발급",
        description = "Refresh Token 으로 새 토큰 쌍을 발급한다. 사용한 Refresh Token 은 폐기된다(Rotation).",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "재발급 성공"),
        ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
        ApiResponse(responseCode = "401", description = "유효하지 않거나 이미 사용된 Refresh Token"),
    )
    @PutMapping("/token")
    fun reissue(
        @RequestBody @Valid request: ReissueRequest,
    ): ResponseEntity<Api<TokenResponse>> =
        Api.OK(TokenResponse.from(authCommandUseCase.reissue(request.toCommand())))

}
