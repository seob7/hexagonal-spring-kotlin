# application/port/in

인바운드 포트. 주도 어댑터(REST 컨트롤러 등)가 호출하는 **유스케이스 인터페이스**를 둔다.

## 규칙

- 인터페이스 이름은 `*UseCase` 접미사를 쓴다.
- **Command / Query 를 분리한다 (CQS).** 쓰기는 `*CommandUseCase`, 읽기는 `*QueryUseCase`.
  하나의 `*Service` 가 두 인터페이스를 함께 구현해도 된다.
- 입력은 HTTP `Request` DTO 가 아니라 application 전용 입력값을 쓴다. 필드가 여러 개면 `dto/` 의 `*Command` 로 묶는다.
  단건 식별자(`referenceId: Long`)는 예외로 그대로 받는다.
- 구현체는 `application/service` 에 둔다. 어댑터는 이 인터페이스에만 의존하고 구현체를 직접 주입하지 않는다.

## 이 패키지의 파일

| 파일 | 설명 |
| :-- | :-- |
| `ReferenceCommandUseCase.kt` | 생성 / 수정 / 삭제 유스케이스 |
| `ReferenceQueryUseCase.kt` | 목록 / 단건 조회 유스케이스 |
| `dto/CreateReferenceCommand.kt` | 생성 입력 모델 |
| `dto/UpdateReferenceCommand.kt` | 수정 입력 모델 |
