# application/port

포트는 application 계층과 바깥 세계 사이의 **계약(interface)** 이다. 구현 세부 사항을 이 경계에서 끊는다.

- `in` — 바깥(어댑터)이 application 을 호출하는 계약. "이 애플리케이션이 할 수 있는 일" 의 목록.
- `out` — application 이 바깥(DB·파일·외부 API)에 요구하는 계약. "이 애플리케이션이 필요로 하는 일".
- 포트에는 Spring MVC, JPA `Repository`, QueryDSL 같은 구현 타입을 노출하지 않는다. 도메인 언어로만 서술한다.
- `in` 의 구현은 `application/service`, `out` 의 구현은 `adapter/out` 에 둔다.

## 헥사고날에서 포트의 방향

```text
        [주도 어댑터]                       [피주도 어댑터]
   adapter.in  ──호출──▶  in port          out port  ◀──구현──  adapter.out
                            │                  ▲
                            ▼                  │
                    application.service ───사용─┘
```

`in` 은 "들어오는 요청"의 계약, `out` 은 "나가는 의존"의 계약이다. 둘 다 application 이 소유한다.
