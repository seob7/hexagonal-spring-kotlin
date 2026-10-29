package com.hexagonal.user.adapter.`in`

import com.hexagonal.common.annotation.WebInputAdapter
import com.hexagonal.user.adapter.`in`.dto.SignUpRequest
import com.hexagonal.user.adapter.`in`.dto.UserResponse
import com.hexagonal.user.application.port.`in`.UserCommandUseCase
import io.github.seob7.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * 인증 없이 호출하는 회원 API. (/open-api)
 */
@Tag(name = "User", description = "회원 API")
@WebInputAdapter("/open-api/user")
class UserOpenApiAdapter(
    private val userCommandUseCase: UserCommandUseCase,
) {

    @Operation(summary = "회원가입", description = "이메일·비밀번호·닉네임으로 회원을 생성한다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "가입 성공"),
        ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
        ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일"),
    )
    @PostMapping
    fun signUp(
        @RequestBody @Valid request: SignUpRequest,
    ): ResponseEntity<Api<UserResponse>> =
        Api.OK(UserResponse.from(userCommandUseCase.signUp(request.toCommand())))

}
