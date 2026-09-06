# adapter/out/persistence

DB 접근용 아웃바운드 포트 구현체.

## 규칙

- `@PersistenceAdapter`(메타: `@Repository`) 를 붙인다.
- `Repository` 를 주입받아 영속화·조회를 위임한다. JPA 쿼리·정렬·fetch 전략 같은 영속성 세부 사항은 이 계층에 둔다.
- 이 프로젝트는 "JPA 엔티티 = 도메인 모델" 이라 `Entity ↔ Domain` 매핑을 강제하지 않는다.
- 논리 삭제된 엔티티는 조회에서 제외한다. 삭제 상태 변경은 도메인의 `softDelete()` 로만 한다.
- 조회 실패 시 `NotFoundException(NotFoundErrorCode.REFERENCE_NOT_FOUND)` 를 던진다. `null` 을 상위로 넘기지 않는다.

## 이 패키지의 파일

| 파일 | 설명 |
| :-- | :-- |
| `ReferencePersistenceAdapter.kt` | `ReferencePersistencePort` 구현. 단순 CRUD 는 `ReferenceRepository`, 페이지 조회는 `ReferenceQueryRepository` 에 위임 |

```kotlin
@PersistenceAdapter
class ReferencePersistenceAdapter(
    private val referenceRepository: ReferenceRepository,
    private val referenceQueryRepository: ReferenceQueryRepository,
) : ReferencePersistencePort {

    override fun findReference(referenceId: Long): Reference =
        referenceRepository.findByIdAndDeletedAtIsNull(referenceId)
            ?: throw NotFoundException(NotFoundErrorCode.REFERENCE_NOT_FOUND)
}
```
