# 코드 컨벤션 명세서

- **목적:** 이 프로젝트 백엔드의 코드 일관성과 정합성을 유지하기 위한 공통 표준 가이드라인입니다.
- 이 문서는 실서비스 프로젝트에서 사용하던 컨벤션을 헥사고날 레퍼런스용으로 정리한 것입니다.

---

## 1. 코드 스타일 및 포맷터

코드 서식 및 정렬은 **구글 코딩 컨벤션**을 표준으로 삼고 IntelliJ 설정을 통일합니다.

- **포맷터 플러그인 적용:**
    - IntelliJ 설정에서 구글 스타일 플러그인을 설치하고 활성화합니다.
    - 상세한 단계별 세팅 방법은 다음 가이드를 참고합니다.
    - [IntelliJ 구글 코딩 컨벤션 설정 가이드](https://systemdata.tistory.com/109)

---

## 2. 네이밍 규칙

코틀린 공식 스타일 가이드를 엄격히 준수합니다.

- **패키지명:** 소문자 단일 단어 조합을 원칙으로 하며, 밑줄(`_`)을 사용하지 않습니다.
    - *Good:* `com.hexagonal.reference.adapter`
    - *Bad:* `com.hexagonal.reference_adapter`
- **클래스 및 인터페이스:** 명사형으로 파스칼 케이스를 사용합니다.
    - *Good:* `ReferenceService`, `ReferenceRepository`
- **메서드 및 변수:** 카멜 케이스를 사용합니다.
    - *Good:* `createReference()`, `referenceId`
- **상수:** `const val`로 선언된 컴파일 타임 상수는 대문자 스네이크 케이스를 사용합니다.
    - *Good:* `const val MAX_TITLE_LENGTH = 100`

---

## 3. 코틀린 기반 JPA 및 롬복 지양 정책

### 3.1 롬복 사용 지양
코틀린은 자체적으로 프로퍼티와 기본 생성자를 지원하므로, 불필요한 라이브러리 의존성을 제거하고 어노테이션 충돌을 방지하기 위해 **롬복** 라이브러리를 지양합니다.

### 3.2 JPA 엔티티 선언 시 데이터 클래스 사용 금지
JPA 엔티티 작성 시 코틀린의 `data class` 사용을 금지하고 일반 `class`를 사용합니다.
- **이유:** 데이터 클래스가 자동으로 생성하는 메서드들이 연관관계 필드 호출 시 프록시의 지연 로딩 오작동 및 순환 참조 에러를 유발합니다.

### 3.3 코틀린 엔티티 권장 구조
기본 생성자 생성을 위해 Gradle에 `kotlin-jpa` 플러그인을 적용하고, 프록시 생성을 위해 `allopen` 플러그인을 활성화한 상태에서 아래와 같이 엔티티를 작성합니다.

```kotlin
@Entity
@Table(name = "references_table")
class Reference(
    @Column(nullable = false, length = 100, comment = "제목")
    var title: String,

    @Column(nullable = false, length = 2_048, comment = "내용")
    var content: String,
) : BaseEntity() {

    // 상태 변경은 의미 있는 메서드로 제공한다. (setter 남발 금지)
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }
}
```

---

## 4. 지연 로딩 정책

### 4.1 지연 로딩 필수 적용
- 모든 N:1 관계와 1:1 관계는 성능 장애 방지를 위해 반드시 **지연 로딩**(`FetchType.LAZY`)으로 명시합니다.
- 코틀린의 클래스와 프로퍼티는 기본적으로 상속이 불가능하므로, JPA 프록시 객체가 정상 작동하도록 `kotlin-spring` 및 `allopen` 플러그인을 Gradle에 필수로 탑재해야 합니다.
- `spring.jpa.open-in-view: false` 를 명시해, 지연 로딩은 애플리케이션 계층(트랜잭션 경계) 안에서만 처리합니다. Input Adapter 에서 엔티티 프로퍼티를 지연 로딩하지 않습니다.

---

## 5. API 및 DTO 설계 규칙

### 5.1 데이터의 완전한 격리
- 어댑터(컨트롤러) 영역에서는 데이터베이스 엔티티 클래스를 요청 본문으로 직접 받거나 응답으로 리턴하지 않습니다.
- 모든 데이터 전송은 전용 DTO 객체(코틀린 `data class`)를 생성하여 처리합니다.
- 계층별 전용 모델을 씁니다: `Request`/`Response`(adapter.in.dto) ↔ `Command`(application.port.in.dto) ↔ `Domain`(domain).

### 5.2 공통 응답 규격 준수
- 성공과 에러를 포함한 모든 API 응답은 사전에 약속한 통합 공통 규격(`io.github.seob7:common-api` 의 `Api<T>`)을 충족하도록 어댑터에서 리턴합니다.
  ```json
  {
    "metaData": {
      "success": true,
      "code": 0,
      "message": "성공적으로 처리되었습니다."
    },
    "data": { }
  }
  ```

### 5.3 요청 검증 (Bean Validation)
- 형식·길이·필수 여부 검증은 **`Request` DTO 에서** 합니다. 어댑터 파라미터에 `@Valid` 를 붙입니다.
- 코틀린은 use-site target 을 명시합니다: `@field:NotBlank`, `@field:Size(max = 100)`.
- 메시지는 한국어로 구체적으로 작성합니다: `"레퍼런스 제목은 100자 이하여야 합니다."`
- 검증 상수(`100`, `2_048`)는 엔티티 `@Column(length = ...)` 와 값을 일치시킵니다.
- 권한·상태 전이·중복 여부는 Request 가 아니라 Service 또는 Domain 에서 검증합니다.

---

## 6. 테스트

- JUnit 5 (`useJUnitPlatform`) + `kotlin-test-junit5` + `spring-boot-starter-test`.
- 위치: `<module>/src/test/kotlin/...` — 프로덕션과 동일 패키지 경로.
- 우선순위: 도메인 엔티티 로직 · 유스케이스(서비스) 단위 테스트를 먼저. 서비스 테스트는 아웃바운드 포트를 대역(fake/mock)으로 두고 검증합니다.
- 어댑터는 슬라이스 테스트로 좁게: 인바운드는 `@WebMvcTest`, 영속성 어댑터는 `@DataJpaTest`.
- 커밋 타입은 `test` 로 분리합니다.

---

## 7. 클린코드

- 함수는 한 가지 일만. 어댑터/서비스가 두꺼워지면 도메인이나 별도 협력 객체로 분리합니다.
- 매직 넘버·문자열은 이름 있는 상수로 뽑습니다.
- 표현식 본문(`= ...`)을 적극 활용하되, 중첩이 깊어지면 블록 본문 + 이른 반환으로 가독성을 택합니다.
- 불필요한 nullable 을 만들지 않습니다. 경계에서 `requireNotNull` / 검증으로 non-null 을 확정합니다.
- 세미콜론을 붙이지 않습니다.
