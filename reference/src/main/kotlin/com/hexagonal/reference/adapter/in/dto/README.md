# adapter/in/dto

인바운드 어댑터가 HTTP 요청을 받고 응답을 돌려줄 때 쓰는 DTO.

## 명명 규칙

- 요청 DTO 는 `*Request`, 응답 DTO 는 `*Response` 접미사를 쓴다.
- `Dto`, `Form`, `Result` 처럼 HTTP 역할이 드러나지 않는 접미사는 쓰지 않는다.
- 생성과 수정은 별도 Request 를 쓴다: `CreateReferenceRequest`, `UpdateReferenceRequest`.

## Request — 검증의 위치

- 외부에서 들어오는 모든 `*Request` 에 필수·길이·형식 검증 어노테이션을 선언한다.
- 코틀린은 use-site target 을 명시한다: `@field:NotBlank`, `@field:Size(max = 100)`.
- 어댑터의 `@RequestBody` 파라미터에 `@Valid` 를 붙여 검증을 실행한다. 중첩 객체·목록은 `@field:Valid` 로 재귀 검증한다.
- 형식·길이·필수 여부만 여기서 본다. 권한·상태 전이·중복 여부는 Service / Domain 에서 검증한다.
- `*Request` 는 `toCommand()` 로 application 의 `*Command` 로 변환한다.

## Response — 변환의 위치

- `data class` 로 선언하고, JPA 어노테이션·지연 로딩 연관관계·상태 변경 메서드를 두지 않는다.
- `companion object` 팩토리 `from(entity)` 로 도메인 엔티티를 응답으로 변환한다. `requireNotNull` 로 `id`·감사 필드의 non-null 을 확정한다.
- 화면에 필요한 필드만 담는다. 습관적으로 전 필드를 노출하지 않는다.

## 이 패키지의 파일

| 파일 | 설명 |
| :-- | :-- |
| `CreateReferenceRequest.kt` | 생성 요청 + 검증 + `toCommand()` |
| `UpdateReferenceRequest.kt` | 수정 요청 + 검증 + `toCommand()` |
| `ReferenceResponse.kt` | 응답 DTO + `from(reference)` 팩토리 |
