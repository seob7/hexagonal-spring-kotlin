# adapter/out/persistence/repository

Spring Data JPA Repository 와 QueryDSL Repository.

## 규칙

- 단순 CRUD·파생 쿼리는 `JpaRepository` 를 상속한 인터페이스로, 조회 의도를 메서드명 또는 `@Query` 로 표현한다.
- 동적·복합 조건 조회는 QueryDSL Repository(`@Repository` 클래스)로 분리한다.
- 필요한 연관관계는 fetch join 또는 `@EntityGraph` 로 명시해 N+1 을 방지한다.
- 논리 삭제를 존중한다: `findByIdAndDeletedAtIsNull(...)`, `where(reference.deletedAt.isNull)`.
- Web DTO, application `Service`, 다른 어댑터를 참조하지 않는다.

## 이 패키지의 파일

| 파일 | 설명 |
| :-- | :-- |
| `ReferenceRepository.kt` | `JpaRepository<Reference, Long>`. 단건 조회 `findByIdAndDeletedAtIsNull` |
| `ReferenceQueryRepository.kt` | QueryDSL 기반 페이지 조회. `PageableExecutionUtils` 로 마지막 페이지 count 쿼리 생략 |

> QueryDSL 의 `JPAQueryFactory` 빈은 실행 애플리케이션의 영속성 컨텍스트를 써야 하므로
> `persistence-common` 이 아니라 `reference/config/QuerydslConfig` 에서 등록한다.
> `QReference` 클래스는 `kapt` 가 빌드 시 생성한다.
