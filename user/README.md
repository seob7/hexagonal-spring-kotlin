# user 모듈

회원가입 · 로그인 · 토큰 관리(재발급·로그아웃) · 내 정보를 제공하는 **실행 가능한 Spring Boot 애플리케이션** (포트 `8081`).
`reference` 와 같은 헥사고날 계층 구조를 따른다. 계층별 일반 규칙은 `reference` 의 각 패키지 README 를 그대로 적용한다.

> 소셜 로그인(Google·Apple)이 아니라 **이메일·비밀번호 기반 자체 인증**이다.
> "OAuth2 Resource Server" 는 Spring Security 의 *JWT 검증 모듈 이름*일 뿐, OAuth2 로그인 프로토콜을 쓰는 게 아니다.

---

## 1. 실행

```bash
# 1) MySQL · Redis
docker compose up -d

# 2) JWT 서명 키 생성 (최초 1회, .keys/ 는 gitignore 대상)
mkdir -p .keys
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out .keys/private.pem
openssl rsa -in .keys/private.pem -pubout -out .keys/public.pem

# 3) 환경 변수 (DB_* 는 reference 와 동일)
export JWT_PRIVATE_KEY_LOCATION=file:$PWD/.keys/private.pem
export JWT_PUBLIC_KEY_LOCATION=file:$PWD/.keys/public.pem

# 4) 실행 → http://localhost:8081/scalar
./gradlew :user:bootRun
```

| 환경 변수 | 필수 | 설명 |
| :-- | :-- | :-- |
| `DB_URL` · `DB_USERNAME` · `DB_PASSWORD` · `DB_NAME` | ✅ | `common/mysql.yaml` |
| `JWT_PRIVATE_KEY_LOCATION` | ✅ | 서명용 개인키 (PKCS#8 PEM). **이 모듈만 가진다** |
| `JWT_PUBLIC_KEY_LOCATION` | ✅ | 검증용 공개키 (X.509 PEM). 다른 모듈도 같은 파일을 쓴다 |
| `REDIS_HOST` · `REDIS_PORT` | | 기본 `localhost:6379` |

---

## 2. API

| 메서드 | 경로 | 인증 | 설명 |
| :-- | :-- | :-- | :-- |
| `POST` | `/open-api/user` | | 회원가입 |
| `POST` | `/open-api/auth/token` | | 로그인 (토큰 발급) |
| `PUT` | `/open-api/auth/token` | | 토큰 재발급 (Refresh Token Rotation) |
| `DELETE` | `/api/auth/token` | ✅ | 로그아웃 (이 기기의 Refresh Token 폐기) |
| `GET` | `/api/user/me` | ✅ | 내 정보 |
| `PUT` | `/api/user/me/password` | ✅ | 비밀번호 변경 → 모든 기기 로그아웃 |
| `DELETE` | `/api/user/me` | ✅ | 회원 탈퇴 → 논리 삭제 + 모든 기기 로그아웃 |

- URL 에 동사(`/login`, `/logout`)를 넣지 않도록 **토큰을 자원으로** 다룬다: 발급 `POST`, 교체 `PUT`, 폐기 `DELETE`.
- `/api/user/me` 처럼 대상 사용자를 경로가 아니라 토큰(`AuthUser`)에서 정한다. 남의 정보에 접근하는 경로 자체가 없다.

### 401 에러 코드 (클라이언트 처리 기준)

| 코드 | 상황 | 클라이언트 동작 |
| :-- | :-- | :-- |
| `UNAUTHORIZED` | 토큰 없이 `/api` 호출 | 로그인 화면 |
| `INVALID_TOKEN` | Access Token 위조·만료 | `PUT /open-api/auth/token` 으로 재발급 |
| `INVALID_REFRESH_TOKEN` | Refresh Token 만료·폐기·재사용 | 로그인 화면 |
| `INVALID_CREDENTIALS` | 이메일 없음 또는 비밀번호 불일치 (구분하지 않음) | 입력값 확인 |

---

## 3. 토큰 정책

| | Access Token | Refresh Token |
| :-- | :-- | :-- |
| 형태 | JWT (RS256 서명) | 불투명(opaque) 난수 32바이트, Base64URL |
| 수명 | 30분 (`hexagonal.jwt.access-token-ttl`) | 14일 (`hexagonal.jwt.refresh-token-ttl`) |
| 저장 | 서버에 저장하지 않음 | Redis. **원문이 아닌 SHA-256 해시**만 저장 |
| 검증 | 각 모듈이 공개키로 (security-common) | Redis 조회 |
| 클레임 | `sub` = userId, `role`, `iat`, `exp` | — |

- **Rotation**: 재발급할 때마다 사용한 Refresh Token 을 폐기하고 새로 발급한다.
  폐기(`DEL`)에 성공한 요청만 재발급되므로, 같은 토큰으로 동시에 요청해도 한 번만 성공한다.
- **로그아웃**: Refresh Token 만 폐기한다. Access Token 은 만료까지 유효하므로 짧게(30분) 둔다.
  즉시 차단이 필요해지면 `jti` 블랙리스트를 추가한다.
- **Redis 키**
  - `refresh-token:{sha256}` → `"{userId}:{expiresAt}"`, TTL = 남은 수명
  - `refresh-token:user:{userId}` → 토큰 해시 Set (비밀번호 변경·탈퇴 시 전체 폐기용)

---

## 4. 패키지 구조

```text
com.hexagonal.user
├── UserApplication.kt
├── config/
│   ├── OpenApiConfig.kt              # + Bearer 보안 스킴
│   ├── JwtProperties.kt              # hexagonal.jwt.* (개인키 위치, TTL)
│   └── JwtEncoderConfig.kt           # 개인키 + security-common 공개키 → JwtEncoder
├── domain/
│   ├── User.kt                       # @Entity. changePassword(), withdraw()
│   ├── UserRole.kt
│   ├── RefreshToken.kt               # 순수 도메인 (JPA 아님)
│   └── AccessToken.kt
├── application/
│   ├── port/in/
│   │   ├── UserCommandUseCase.kt     # signUp, changePassword, withdraw
│   │   ├── UserQueryUseCase.kt       # findUser
│   │   ├── AuthCommandUseCase.kt     # login, reissue, logout
│   │   └── dto/                      # *Command + TokenResult(결과 모델)
│   ├── port/out/
│   │   ├── UserPersistencePort.kt
│   │   ├── RefreshTokenPersistencePort.kt
│   │   ├── PasswordEncoderPort.kt
│   │   └── TokenIssuerPort.kt
│   └── service/
│       ├── UserService.kt
│       └── AuthService.kt
└── adapter/
    ├── in/
    │   ├── UserOpenApiAdapter.kt     # /open-api/user
    │   ├── UserApiAdapter.kt         # /api/user
    │   ├── AuthOpenApiAdapter.kt     # /open-api/auth
    │   ├── AuthApiAdapter.kt         # /api/auth
    │   └── dto/                      # *Request, *Response, PasswordRule
    └── out/
        ├── persistence/
        │   ├── UserPersistenceAdapter.kt          # JPA
        │   ├── RefreshTokenPersistenceAdapter.kt  # Redis (StringRedisTemplate)
        │   └── repository/UserRepository.kt
        ├── security/BCryptPasswordEncoderAdapter.kt
        └── token/TokenIssuerAdapter.kt
```

`AuthService` 는 BCrypt · JWT · Redis 를 전혀 모른다. 네 개의 아웃 포트만 조합한다.

---

## 5. reference 와 다른 점

### 5.1 두 가지 영속성 스타일이 공존한다

| | `User` | `RefreshToken` |
| :-- | :-- | :-- |
| 도메인 = 저장 모델? | 예 (`@Entity`) — 루트 README 4.4 #2 의 타협 | 아니오. 순수 클래스, 매핑은 어댑터가 |
| 수정 반영 | JPA 더티 체킹 (4.4 #1) | 포트의 명시적 `save` / `delete` |

Redis 에는 더티 체킹이 없다. 4.4 #1 의 "아웃 어댑터를 바꾸면 수정이 조용히 저장되지 않는다" 가 실제로 어떤 의미인지,
`RefreshToken` 쪽이 정통 방식으로 어떻게 풀었는지 비교해 볼 수 있다.

### 5.2 유스케이스가 엔티티 대신 결과 모델을 반환한다

토큰은 엔티티가 아니므로 `AuthCommandUseCase` 는 `TokenResult`(`port/in/dto`)를 돌려준다. (4.4 #6 을 푸는 방향)

### 5.3 `@OutputAdapter`

BCrypt · JWT 어댑터는 웹도 영속성도 아니라서 `common` 에 `@OutputAdapter`(메타: `@Component`)를 추가해 쓴다.

---

## 6. 설계 결정

- **이메일 unique × 논리 삭제**: 탈퇴 회원 행이 남아도 재가입할 수 있게, `withdraw()` 가 이메일을 `withdrawn-{id}@withdrawn.invalid` 로 바꾼다.
  MySQL 은 NULL 을 서로 다른 값으로 보므로 `unique(email, deleted_at)` 로는 활성 회원 중복을 막지 못한다.
- **동시 가입**: 서비스의 중복 검사를 두 요청이 함께 통과하면 unique 제약이 막는다. 어댑터가 `saveAndFlush` 로 즉시 flush 해서 제약 위반을 409 로 바꾼다.
- **가입 여부 비노출**: 로그인 시 "이메일 없음" 과 "비밀번호 불일치" 를 같은 `INVALID_CREDENTIALS` 로 응답한다.
- **비밀번호 규칙**: 8~64자, 출력 가능한 ASCII 만. BCrypt 가 72바이트를 넘는 입력을 거부하기 때문이다. 로그인 요청에는 형식 규칙을 걸지 않는다.
- **현재 비밀번호 불일치는 400**: 401 로 내리면 클라이언트가 "토큰 만료" 로 오해해 재발급을 시도한다.

---

## 7. 테스트

```bash
./gradlew :user:test
```

서비스 단위 테스트는 아웃 포트를 인메모리 fake(`src/test/.../fake`)로 바꿔 검증한다. Spring 컨텍스트·DB·Redis 없이 돈다.

| 테스트 | 검증 내용 |
| :-- | :-- |
| `AuthServiceTest` | 로그인 성공·실패(없는 이메일 포함), 재발급 Rotation, 재사용 토큰 거부, 로그아웃 범위(이 기기만 / 남의 토큰 불가) |
| `UserServiceTest` | 비밀번호 인코딩, 중복 이메일, 현재 비밀번호 불일치, 비밀번호 변경·탈퇴 시 전체 토큰 폐기, 탈퇴 후 재가입 |
