# application

유스케이스 계층. 도메인 규칙 실행을 **조율(orchestration)** 하고 트랜잭션 경계를 관리한다.
직접 구현이 아니라 포트(계약) 뒤에서 동작한다.

## 의존 방향

```text
adapter.in  ──▶  application.port.in (UseCase)
                          │  구현
                          ▼
                 application.service  ──▶  domain
                          │  사용
                          ▼
                 application.port.out  ◀──  adapter.out (구현)
```

- application 은 **domain 과 자기 port 만** 의존한다. Spring Web, JPA Repository, `Request`/`Response` DTO 를 import 하지 않는다.
  - 이 구현에서 허용하는 예외: `@Service`·`@Transactional`, 그리고 포트 시그니처의 `Pageable`/`Page` (루트 README 4.4 #3·#5).
- 외부에서의 진입은 `port.in`, 외부로의 의존은 `port.out` 인터페이스로만 표현한다.
- 비즈니스 규칙 자체는 도메인 엔티티로 밀어넣는다. 서비스는 흐름 조립·권한/상태 검증·트랜잭션만 담당한다.

## 하위 패키지

| 패키지 | 역할 |
| :-- | :-- |
| `port/in` | 인바운드 포트 = 유스케이스 인터페이스(`*UseCase`)와 입력 모델(`*Command`) |
| `port/out` | 아웃바운드 포트 = application 이 외부(DB 등)에 요구하는 계약 |
| `service` | 인바운드 포트 구현체. 유스케이스 흐름과 트랜잭션 경계 |
