# application/port/in/dto

유스케이스 호출에 필요한 application 전용 입력 DTO(`*Command`).

## 규칙

- 이름은 `*Command` 접미사를 쓴다.
- HTTP·Jackson·Bean Validation 어노테이션을 두지 않는다. 프레임워크에서 자유로운 순수 데이터다.
- 상태 전달이 목적이므로 `data class` 를 쓴다.
- 형식·길이 검증은 이미 `adapter/in/dto` 의 `*Request` 에서 끝났다고 본다. Command 는 검증된 값의 운반체다.
- 어댑터에서 `Request.toCommand()` 로 만들고, 서비스에서 도메인 객체 생성 또는 도메인 메서드 호출로 푼다.
  Command 를 도메인 엔티티에 그대로 넘기거나 필드로 보관하지 않는다.

```kotlin
data class CreateReferenceCommand(
    val title: String,
    val content: String,
)
```
