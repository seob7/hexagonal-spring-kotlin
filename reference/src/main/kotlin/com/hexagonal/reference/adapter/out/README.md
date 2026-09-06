# adapter/out

피주도(driven) 어댑터. 아웃바운드 포트를 **구현**한다.

## 규칙

- JPA, 외부 HTTP Client, 파일 저장소 등 기술 구현은 전부 이 계층에 둔다.
- application 계층은 이 구현체가 아니라 아웃바운드 포트 인터페이스에만 의존한다. (의존성 역전)
- 조회 실패는 여기서 공통 예외로 변환한다. 포트 반환 타입은 non-null 로 유지하고, 없으면 `NotFoundException` 을 던진다.
- 논리 삭제된 레코드는 조회 조건에서 제외한다 (`...AndDeletedAtIsNull`).

## 이 패키지의 구성

| 경로 | 설명 |
| :-- | :-- |
| `persistence/ReferencePersistenceAdapter.kt` | `ReferencePersistencePort` 구현체. `@PersistenceAdapter` |
| `persistence/repository/` | Spring Data JPA / QueryDSL Repository — 아래 하위 README 참고 |

## `get*` vs `find*`

| 접두사 | 반환 특성 | 사용처 |
| :-- | :-- | :-- |
| `get*` | nullable | "없을 수 있음" 을 호출자에게 알리는 조회 |
| `find*` | non-null, 없으면 예외 | 대상이 있어야 정상인 조회. 이 어댑터의 `findReference()` 가 그 예 |
