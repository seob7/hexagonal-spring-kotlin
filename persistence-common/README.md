# persistence-common 모듈

영속성 공용 코드. `java-library` + `kotlin-jpa` / `allopen` / `kapt` 로 빌드한다. 실행 파일이 아니다.

| 파일 | 내용 |
| :-- | :-- |
| `persistence/BaseEntity.kt` | 모든 JPA 엔티티가 상속하는 공통 메타데이터: `@Tsid` 식별자, `createdAt` / `updatedAt`(JPA Auditing), `deletedAt` + `softDelete()` |
| `persistence/response/PageResponse.kt` | 목록 API 공통 페이지 응답 포맷. 1-based `currentPage` 로 변환 |

## 주의

- `BaseEntity` 의 감사 필드는 JPA Auditing 이 채운다. **실행 애플리케이션에 `@EnableJpaAuditing` 이 필수**다.
- 식별자는 DB auto-increment 가 아니라 애플리케이션에서 생성하는 TSID(`Long`)다.
- `id` / `createdAt` / `updatedAt` / `deletedAt` 의 setter 는 `protected` 다. 외부에서 바꾸지 않는다.
- QueryDSL `JPAQueryFactory` 빈은 이 모듈이 아니라 실행 모듈(`reference/config/QuerydslConfig`)에서 등록한다. 영속성 컨텍스트가 실행 모듈 소유이기 때문이다.
