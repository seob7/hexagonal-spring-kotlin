# security-common 모듈

모든 실행 모듈이 공유하는 **인증(토큰 검증)** 라이브러리. `java-library` 로 빌드하며 실행 파일이 아니다.
토큰을 **발급하지 않는다.** 발급은 `user` 모듈만 한다.

```text
  user (발급: 개인키)  ──JWT──▶  클라이언트  ──Bearer JWT──▶  어느 실행 모듈이든 (검증: 공개키)
                                                               └ security-common
```

공개키만 있으면 검증할 수 있으므로, 각 모듈은 user 모듈을 호출하지 않고 스스로 토큰을 검증한다.
개인키가 없으니 토큰을 위조할 수도 없다. (RS256 을 고른 이유)

## 하는 일

| 파일 | 내용 |
| :-- | :-- |
| `config/SecurityConfig.kt` | 필터 체인. `/open-api` 하위·API 문서·`/error` 는 허용, 그 외(`/api` 하위 포함)는 Access Token 필요. STATELESS, CSRF·폼 로그인 비활성 |
| `config/JwtDecoderConfig.kt` | RS256 공개키로 서명·만료를 검증하는 `JwtDecoder`. 공개키는 `RSAPublicKey` 빈으로도 노출 (user 모듈의 `JwtEncoder` 가 같이 씀) |
| `config/AuthUserWebConfig.kt` | `AuthUserArgumentResolver` 등록, API 문서에서 `AuthUser` 파라미터 숨김 |
| `AuthUser.kt` | 컨트롤러가 받는 인증 사용자 모델 (`userId`, `role`). Spring Security 타입이 어댑터로 새지 않게 하는 경계 |
| `JwtClaims.kt` | 발급·검증 양쪽이 공유하는 클레임 계약. `sub` = userId, `role` = 권한 |
| `handler/ApiAuthenticationEntryPoint.kt` | 401 을 `Api.ERROR` 규격으로 응답. 토큰 없음 → `UNAUTHORIZED`, 위조·만료 → `INVALID_TOKEN` |
| `handler/ApiAccessDeniedHandler.kt` | 403 을 `Api.ERROR` 규격으로 응답 |
| `resources/security.yaml` | 공개키 위치 설정 조각. 실행 모듈이 `spring.config.import` 로 가져간다 |

> 필터에서 난 예외는 `@RestControllerAdvice` 에 닿지 않는다. 그래서 EntryPoint 가 `HandlerExceptionResolver` 로 예외를 넘겨,
> common 의 `UnauthorizedExceptionHandler` / `ForbiddenExceptionHandler` 가 컨트롤러 예외와 똑같은 응답을 만들게 했다.

## 실행 모듈에 붙이는 법

1. `build.gradle.kts`

   ```kotlin
   implementation(project(":security-common"))
   ```

2. `application.yaml`

   ```yaml
   spring:
     config:
       import:
         - "classpath:mysql.yaml"
         - "classpath:security.yaml"
   ```

3. 환경 변수 `JWT_PUBLIC_KEY_LOCATION=file:/절대경로/.keys/public.pem`

4. 인증이 필요한 어댑터는 `/api/...` 경로에 두고, 파라미터로 `AuthUser` 를 받는다.

   ```kotlin
   @GetMapping("/me")
   fun findMe(authUser: AuthUser): ResponseEntity<Api<UserResponse>> = ...
   ```

   역할 제한이 필요하면 `SecurityConfig` 에 `authorize("/api/admin/**", hasRole("ADMIN"))` 처럼 추가한다.

## 주의

- 이 모듈을 의존하는 순간 **`/open-api` 하위가 아닌 모든 경로가 인증 대상**이 된다. 경로 prefix 규칙(`/open-api` vs `/api`)을 반드시 지킨다.
- 실행 모듈끼리는 서로 의존하지 않는다. 사용자 정보가 더 필요하면 JWT 클레임을 늘리거나, 해당 모듈에 아웃 포트 + `@WebOutputAdapter` 로 user API 를 호출한다.
- 만료 검증에는 기본 60초의 clock skew 허용이 있다. 만료 직후 1분 동안은 토큰이 통과할 수 있다.
