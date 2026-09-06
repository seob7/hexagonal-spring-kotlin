# application/port/out

아웃바운드 포트. application 이 DB·파일 저장소·외부 API 에 요구하는 **계약**을 둔다.

## 규칙

- 인터페이스는 필요한 **행위**를 도메인 언어로 표현한다: `create`, `findReference`, `findReferences`.
  특정 `Repository` / `HttpClient` / QueryDSL 타입을 노출하지 않는다.
- 반환 타입은 non-null 로 둔다. 대상이 없으면 구현체(`adapter/out`)에서 공통 예외로 끊고, 상위 계층에 `null` 을 흘리지 않는다.
- 구현체는 `adapter/out` 에 둔다. application 은 구현체가 아니라 이 인터페이스에만 의존한다. (의존성 역전)
- ⚠️ 지금 이 포트에는 **`create` 만 있고 `save`/`update` 가 없다.** 수정은 서비스가 조회한 엔티티를 변경하면 JPA 더티 체킹이 반영하기 때문이다(루트 README 4.4 #1). 정통이라면 여기에 명시적 `save(reference)` 를 두고 서비스가 호출한다.
- ⚠️ `findReferences(pageable)` 의 `Pageable`/`Page` 는 `org.springframework.data.domain` 타입이다(4.4 #3). 정통이라면 자체 `PageQuery`/`PageResult` 를 정의한다.

## 이 패키지의 파일

| 파일 | 설명 |
| :-- | :-- |
| `ReferencePersistencePort.kt` | 레퍼런스 영속화·조회 계약. 구현은 `adapter/out/persistence/ReferencePersistenceAdapter` |

## `get*` vs `find*`

| 접두사 | 반환 특성 | 의미 |
| :-- | :-- | :-- |
| `get*` | nullable — `null` 반환 가능 | "없을 수도 있음" 을 호출자에게 위임 |
| `find*` | non-null — 못 찾으면 예외 | "있어야 정상" 인 조회. 이 포트는 이 방식을 쓴다 |
