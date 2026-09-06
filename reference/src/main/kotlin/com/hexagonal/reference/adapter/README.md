# adapter

어댑터는 외부 세계와 application 을 잇는다. 기술(HTTP, JPA, 외부 API)은 전부 이 계층에 격리된다.

```text
        ┌─────────────── adapter ───────────────┐
HTTP ──▶│  in  ──▶ [in port]        [out port] ◀── out  ──▶ DB / 외부 시스템
        └───────────────────────────────────────┘
```

- `in` — 주도(driving) 어댑터. HTTP 등 외부 요청을 유스케이스 호출로 변환한다.
- `out` — 피주도(driven) 어댑터. 아웃바운드 포트를 구현해 DB·외부 시스템과 통신한다.
- 어댑터는 application 의 **포트(인터페이스)** 에만 의존한다. 반대 방향(application → adapter) 의존을 만들지 않는다.
- 어댑터끼리 직접 호출하지 않는다. 항상 포트를 경유한다.

## 커스텀 어댑터 어노테이션 (`com.hexagonal.common.annotation`)

프레임워크 어노테이션을 직접 쓰지 않고 의도를 드러내는 래퍼를 쓴다.

| 어노테이션 | 메타 | 용도 |
| :-- | :-- | :-- |
| `@WebInputAdapter(vararg path)` | `@RestController` + `@RequestMapping` | 인바운드 REST 어댑터. 생성자 인자로 base path 지정 |
| `@WebOutputAdapter` | `@Component` | 아웃바운드 어댑터(외부 API 호출 등) |
| `@PersistenceAdapter` | `@Repository` | 아웃바운드 영속성 어댑터 (예외 변환 포함) |
