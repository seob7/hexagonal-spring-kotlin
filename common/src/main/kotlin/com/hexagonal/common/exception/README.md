# 공통 예외 처리 (`com.hexagonal.common.exception`)

모든 모듈이 공유하는 예외 타입과 전역 핸들러다.
도메인·영속성 계층에서 **의미 있는 예외를 던지기만 하면**, 핸들러가 공통 응답 규격(`Api`)에 맞는 에러 응답을 자동으로 만든다.

---

## 1. 구성 요소

| 요소 | 위치 | 역할 |
| :-- | :-- | :-- |
| `BaseErrorCode` | `io.github.seob7` (common-api) | 에러 코드 계약. `getHttpStatus()`, `getMessage()` 두 개만 정의 |
| `NotFoundErrorCode` | `exception.errorcode` | "리소스 없음" 계열 에러 코드 enum. `BaseErrorCode` 구현 |
| `ValidationErrorCode` | `exception.errorcode` | "요청 값 검증 실패" 계열 에러 코드 enum |
| `NotFoundException` | `exception` | `BaseErrorCode` 를 담는 `RuntimeException` |
| `ValidationException` | `exception` | 비즈니스 로직에서 명시적으로 던지는 검증 실패 예외 |
| `NotFoundExceptionHandler` | `exception.handler` | `@RestControllerAdvice`. `NotFoundException` → `Api.ERROR` |
| `ValidationExceptionHandler` | `exception.handler` | `ValidationException` 과 `@Valid` 실패(`MethodArgumentNotValidException`) → `Api.ERROR` |

### 처리 흐름

```text
영속성 어댑터 / 서비스
        │  throw NotFoundException(NotFoundErrorCode.REFERENCE_NOT_FOUND)
        ▼
NotFoundExceptionHandler  (@RestControllerAdvice)
        │  log.warn(...) + Api.ERROR(errorCode)
        ▼
HTTP 404  +  공통 JSON 바디
```

---

## 2. 사용법

"찾을 수 없음" 이 확정되는 지점(보통 아웃바운드 영속성 어댑터)에서 던진다.

```kotlin
// reference/adapter/out/persistence/ReferencePersistenceAdapter.kt
override fun findReference(referenceId: Long): Reference =
    referenceRepository.findByIdAndDeletedAtIsNull(referenceId)
        ?: throw NotFoundException(NotFoundErrorCode.REFERENCE_NOT_FOUND)
```

- 포트 반환 타입은 **non-null** 로 두고, 없으면 여기서 예외로 끊는다.
- 상위 계층(서비스·어댑터)은 `null` 체크도 `try-catch` 도 하지 않는다. 정상 흐름만 작성한다.
- 원인 예외를 감쌀 땐 두 번째 인자로 전달한다: `NotFoundException(code, cause)`.

`@Valid` 검증 실패는 Spring 이 던지는 `MethodArgumentNotValidException` 을 `ValidationExceptionHandler` 가 받아,
필드별 메시지를 모아 `Api.ERROR` 로 반환한다.

---

## 3. 새 에러 코드 추가

같은 계열이면 enum 에 상수만 추가한다.

```kotlin
enum class NotFoundErrorCode(
    private val httpStatus: HttpStatus,
    private val message: String,
) : BaseErrorCode {

    REFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Reference is not found."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User is not found."),   // 추가
    ;
    override fun getHttpStatus() = httpStatus
    override fun getMessage() = message
}
```

- 상수명: `<도메인>_NOT_FOUND` (대문자 스네이크 케이스), HTTP 상태는 항상 404.

---

## 4. 새 예외 계열 추가

`NotFound` 패턴을 그대로 복제해 3종 세트를 만든다.

1. **에러 코드 enum** — `errorcode/XxxErrorCode.kt`, `BaseErrorCode` 구현
2. **예외 클래스** — `XxxException(val baseErrorCode: BaseErrorCode, cause: Throwable? = null) : RuntimeException(...)`
3. **핸들러** — `handler/XxxExceptionHandler.kt`, `@RestControllerAdvice` + `@ExceptionHandler` → `Api.ERROR(e.baseErrorCode)`

> `BaseErrorCode` 계약 덕분에 변환 코드는 모두 동일하다. 핸들러가 많아지면 한 `@RestControllerAdvice` 클래스에 `@ExceptionHandler` 메서드를 모아도 된다.

---

## 5. 애플리케이션 설정 요구사항 ⚠️

핸들러가 **common 모듈**에 있으므로, 실행 모듈은 컴포넌트 스캔 범위에 `com.hexagonal` 을 포함해야 한다.

```kotlin
@SpringBootApplication(scanBasePackages = ["com.hexagonal"])
class ReferenceApplication
```

이 설정이 없으면 `@RestControllerAdvice` 빈이 등록되지 않아, 예외가 그대로 500 으로 떨어진다.