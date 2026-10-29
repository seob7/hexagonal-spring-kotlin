package com.hexagonal.user.adapter.`in`

import com.hexagonal.common.annotation.WebInputAdapter
import com.hexagonal.security.AuthUser
import com.hexagonal.user.adapter.`in`.dto.ChangePasswordRequest
import com.hexagonal.user.adapter.`in`.dto.UserResponse
import com.hexagonal.user.application.port.`in`.UserCommandUseCase
import com.hexagonal.user.application.port.`in`.UserQueryUseCase
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * 로그인한 사용자 본인의 회원 API. (/api — Access Token 필요)
 * 대상 사용자는 경로가 아니라 토큰에서 정해지므로(`AuthUser`), 다른 사람의 정보에 접근할 수 없다.
 */
@Tag(name = "User")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
@WebInputAdapter("/api/user")
class UserApiAdapter(
    private val userCommandUseCase: UserCommandUseCase,
    private val userQueryUseCase: UserQueryUseCase,
) {

    @Operation(summary = "내 정보 조회", description = "Access Token 의 사용자 정보를 조회한다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
    )
    @GetMapping("/me")
    fun findMe(authUser: AuthUser): ResponseEntity<Api<UserResponse>> =
        Api.OK(UserResponse.from(userQueryUseCase.findUser(authUser.userId)))

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인한 뒤 변경한다. 모든 기기의 Refresh Token 이 폐기된다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "변경 성공"),
        ApiResponse(responseCode = "400", description = "요청 값 검증 실패 또는 현재 비밀번호 불일치"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
    )
    @PutMapping("/me/password")
    fun changePassword(
        authUser: AuthUser,
        @RequestBody @Valid request: ChangePasswordRequest,
    ): ResponseEntity<Api<Boolean>> {
        userCommandUseCase.changePassword(authUser.userId, request.toCommand())
        return Api.OK(true)
    }

    @Operation(summary = "회원 탈퇴", description = "논리 삭제하고 모든 기기의 Refresh Token 을 폐기한다. 같은 이메일로 재가입할 수 있다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "탈퇴 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
    )
    @DeleteMapping("/me")
    fun withdraw(authUser: AuthUser): ResponseEntity<Api<Boolean>> {
        userCommandUseCase.withdraw(authUser.userId)
        return Api.OK(true)
    }

}
