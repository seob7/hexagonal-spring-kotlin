# common 모듈

전 모듈이 공유하는 순수 공용 라이브러리. `java-library` 로 빌드하며 실행 파일(BootJar)이 아니다.

| 패키지 | 내용 |
| :-- | :-- |
| `annotation` | 어댑터 의도를 드러내는 커스텀 어노테이션 (`@WebInputAdapter`, `@WebOutputAdapter`, `@PersistenceAdapter`) |
| `exception` | 공통 예외 타입 + 전역 `@RestControllerAdvice` 핸들러 → [상세 README](src/main/kotlin/com/hexagonal/common/exception/README.md) |
| `resources/mysql.yaml` | 공유 DataSource 설정 조각. 실행 모듈이 `spring.config.import` 로 가져간다 |

- 공통 응답 규격(`Api<T>`, `BaseErrorCode`)은 외부 라이브러리 `io.github.seob7:common-api` 에서 온다.
- 이 모듈에는 도메인 개념을 두지 않는다. 특정 도메인에 묶이면 해당 도메인 모듈로 옮긴다.
