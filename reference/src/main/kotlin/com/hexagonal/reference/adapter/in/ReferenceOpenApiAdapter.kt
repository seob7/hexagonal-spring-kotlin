package com.hexagonal.reference.adapter.`in`

import com.hexagonal.common.annotation.WebInputAdapter
import com.hexagonal.persistence.response.PageResponse
import com.hexagonal.reference.adapter.`in`.dto.CreateReferenceRequest
import com.hexagonal.reference.adapter.`in`.dto.ReferenceResponse
import com.hexagonal.reference.adapter.`in`.dto.UpdateReferenceRequest
import com.hexagonal.reference.application.port.`in`.ReferenceCommandUseCase
import com.hexagonal.reference.application.port.`in`.ReferenceQueryUseCase
import io.github.seob7.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

/**
 * Web Input Adapter 어노테이션 명시적으로 지정
 * 인증이 없는 API 에 대해 /open-api prefix 를 추가한다.
 * 인증이 있다면 /api prefix 를 추가한다.
 */
@Tag(name = "Reference", description = "레퍼런스 CRUD API")
@WebInputAdapter("/open-api/reference")
class ReferenceOpenApiAdapter(
    private val referenceCommandUseCase: ReferenceCommandUseCase,
    private val referenceQueryUseCase: ReferenceQueryUseCase,
) {

    /**
     * - 메서드 생성 순서는 C -> R -> U -> D 순으로 작성한다.
     * - Restful 을 지향한다.
     */
    // 조회 없이 바로 저장하므로 404 는 발생하지 않는다.
    @Operation(summary = "레퍼런스 생성", description = "제목과 내용을 입력받아 레퍼런스를 생성한다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "생성 성공"),
        ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
    )
    @PostMapping
    fun createReference(
        @RequestBody @Valid request: CreateReferenceRequest
    ): ResponseEntity<Api<ReferenceResponse>> =
        Api.OK(ReferenceResponse.from(referenceCommandUseCase.createReference(request.toCommand())))

    @Operation(summary = "레퍼런스 목록 조회", description = "페이지 단위로 레퍼런스 목록을 조회한다. id 내림차순(최신순)으로 정렬된다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping
    fun findReferences(
        @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<Api<PageResponse<ReferenceResponse>>> {
        val referencePage = referenceQueryUseCase.findReferences(PageRequest.of(page - 1, size))
        return Api.OK(PageResponse.from(referencePage) { ReferenceResponse.from(it) })
    }

    @Operation(summary = "레퍼런스 단건 조회", description = "레퍼런스 ID 로 단건을 조회한다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "레퍼런스를 찾을 수 없음"),
    )
    @GetMapping("/{referenceId}")
    fun findReference(
        @Parameter(description = "조회할 레퍼런스 ID") @PathVariable referenceId: Long,
    ): ResponseEntity<Api<ReferenceResponse>> =
        Api.OK(ReferenceResponse.from(referenceQueryUseCase.findReference(referenceId)))

    @Operation(summary = "레퍼런스 수정", description = "레퍼런스 ID 로 제목과 내용을 수정한다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
        ApiResponse(responseCode = "404", description = "레퍼런스를 찾을 수 없음"),
    )
    @PutMapping("/{referenceId}")
    fun updateReference(
        @Parameter(description = "수정할 레퍼런스 ID") @PathVariable referenceId: Long,
        @RequestBody @Valid request: UpdateReferenceRequest,
    ): ResponseEntity<Api<ReferenceResponse>> =
        Api.OK(
            ReferenceResponse.from(
                referenceCommandUseCase.updateReference(
                    referenceId, request.toCommand()
                )
            )
        )

    @Operation(summary = "레퍼런스 삭제", description = "레퍼런스 ID 로 논리 삭제한다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "삭제 성공"),
        ApiResponse(responseCode = "404", description = "레퍼런스를 찾을 수 없음"),
    )
    @DeleteMapping("/{referenceId}")
    fun deleteReference(
        @Parameter(description = "삭제할 레퍼런스 ID") @PathVariable referenceId: Long,
    ): ResponseEntity<Api<Boolean>> {
        referenceCommandUseCase.deleteReference(referenceId)
        return Api.OK(true)
    }

}
