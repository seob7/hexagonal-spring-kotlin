# adapter/in

주도(driving) 어댑터. HTTP 요청을 받아 인바운드 포트(유스케이스)를 호출한다.

## 규칙

- `*UseCase` 인터페이스에만 의존한다. `*Service` 구현체를 직접 주입하지 않는다.
- 컨트롤러는 얇게 유지한다: `Request.toCommand()` 변환 → 유스케이스 호출 → `Response.from(...)` 변환. 분기·비즈니스 로직 금지.
- 엔티티를 HTTP 요청·응답으로 직접 쓰지 않는다. `Request` / `Response` DTO 로 경계를 친다.
- 요청 검증(`@Valid`)은 이 계층에서 시작한다.
- `Request` 나 `Command` 를 도메인 엔티티에 직접 전달하지 않는다.

## 경로·메서드 규칙

- 인증이 없는 API 는 `/open-api/...`, 인증이 필요한 API 는 `/api/...` prefix 를 쓴다.
- RESTful 지향: 자원 중심 URL, 행위는 HTTP 메서드로. URL 에 `createReference` 같은 동사를 넣지 않는다.
- 메서드 작성 순서는 **C → R → U → D** 로 고정한다.
- 모든 응답은 `io.github.seob7:common-api` 의 `Api<T>` 로 감싼다: `ResponseEntity<Api<ReferenceResponse>>`, `Api.OK(body)`.

## 이 패키지의 파일

| 파일 | 설명 |
| :-- | :-- |
| `ReferenceOpenApiAdapter.kt` | 레퍼런스 CRUD REST 어댑터. `@WebInputAdapter("/open-api/reference")` |
| `dto/` | `Request` / `Response` DTO — 아래 `dto/README.md` 참고 |
