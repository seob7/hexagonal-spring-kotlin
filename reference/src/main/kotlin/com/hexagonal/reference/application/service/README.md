# application/service

인바운드 포트(`*UseCase`) 구현체. 유스케이스 흐름을 조율하고 트랜잭션 경계를 선언한다.

## 규칙

- 트랜잭션은 클래스에 `@Transactional(readOnly = true)` 를 기본값으로 두고, **쓰기 메서드에만** `@Transactional` 을 재정의한다.
- **수정·삭제는 현재 JPA 더티 체킹으로 반영된다.** 조회한 엔티티의 도메인 메서드(`update()`, `softDelete()`)를 호출하면 커밋 시 저장되고, `save()` 를 다시 부르지 않는다.
  - ⚠️ 이건 이 구현이 택한 타협이다(루트 README 4.4 #1). 코어 동작이 Hibernate 라는 기술에 묶여서, 아웃 어댑터를 인메모리·REST 로 바꾸면 수정이 저장되지 않는다.
  - 정통으로 가려면 아웃 포트에 명시적 `save(reference)` 를 두고 서비스가 `update()` 후 그걸 호출한다. → 더티 체킹은 여전히 런타임에 동작하지만, *코드가 그것에 의존하지 않게* 된다.
- DB 접근은 아웃바운드 포트(`ReferencePersistencePort`)로만 한다. `Repository` 를 직접 주입하지 않는다.
- `Command` 를 도메인 객체 생성 인자 또는 도메인 메서드 인자로 푼다. 엔티티에 `Command` 를 넘기거나 보관하지 않는다.
- 비즈니스 규칙은 도메인으로 밀어넣는다. 서비스는 "조회 → 도메인 메서드 호출 → (필요 시) 저장" 흐름만 갖는다.

## 이 패키지의 파일

| 파일 | 설명 |
| :-- | :-- |
| `ReferenceService.kt` | `ReferenceCommandUseCase` + `ReferenceQueryUseCase` 를 함께 구현. 포트 하나(`ReferencePersistencePort`)에만 의존 |

```kotlin
@Service
@Transactional(readOnly = true)
class ReferenceService(
    private val referencePersistencePort: ReferencePersistencePort,
) : ReferenceCommandUseCase, ReferenceQueryUseCase {

    @Transactional
    override fun updateReference(referenceId: Long, command: UpdateReferenceCommand): Reference =
        referencePersistencePort.findReference(referenceId).apply {
            update(command.title, command.content)   // 더티 체킹으로 반영
        }
}
```
