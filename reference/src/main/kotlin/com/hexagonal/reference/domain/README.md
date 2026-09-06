# domain

도메인 계층. 비즈니스 개념과 **상태 변경 규칙**의 중심이다. 의존성 화살표는 항상 이 안쪽을 향한다.

## 규칙

- **application·adapter 를 의존하지 않는다.** 포트·유스케이스·Command·Spring Web·Repository 구현을 import 하지 않는다.
- JPA 매핑 어노테이션 + `BaseEntity` 상속은 **이 구현이 택한 타협**이다 (루트 README 4.4 #2·#4). "도메인 모델 = JPA 엔티티" 방식이라 별도의 `Entity ↔ Domain` 매핑을 만들지 않는다. 그 대가로 도메인이 `jakarta.persistence` 와 `persistence-common` 에 컴파일 의존한다. 정통이라면 도메인은 순수 POJO 이고 JPA 엔티티는 `adapter/out` 에 따로 둔다.
- `data class` 를 쓰지 않는다. 일반 `class` 로 선언한다. (프록시 지연 로딩 오작동·순환 참조 방지)
- 상태 변경은 setter 가 아니라 **의미 있는 메서드**로 노출한다. (`update()`, `softDelete()`)
- 엔티티 생성자에 application 의 `Command` 를 넘기지 않는다. 도메인 값 또는 값 객체(`@Embeddable`)를 받는다.
- 모든 N:1 / 1:1 연관관계는 `FetchType.LAZY` 를 명시한다.
- `@Column` 에 `length`, `comment` 를 채워 스키마 의도를 남기고, 테이블명은 `@Table(name = ...)` 로 고정한다.

## 이 패키지의 파일

| 파일 | 설명 |
| :-- | :-- |
| `Reference.kt` | 레퍼런스 엔티티. `title` / `content` 와 상태 변경 메서드 `update()` 를 가진다. 공통 메타데이터(`id`, 감사 필드, 논리 삭제)는 `com.hexagonal.persistence.BaseEntity` 에서 상속한다. |

```kotlin
@Entity
@Table(name = "references_table")
class Reference(
    @Column(nullable = false, length = 100, comment = "제목")
    var title: String,
    @Column(nullable = false, length = 2_048, comment = "내용")
    var content: String,
) : BaseEntity() {

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }
}
```

> 논리 삭제는 `BaseEntity.softDelete()` 로 `deletedAt` 에 시각을 찍는다. 물리 삭제하지 않는다.
