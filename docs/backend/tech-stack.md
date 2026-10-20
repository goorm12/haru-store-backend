# 하루상점 백엔드 기술 스택 명세서 (Tech Stack)

- **작성일:** 2026-10-20
- **문서 버전:** v0.1
- **대상:** `haru-store-backend`
- **목적:** 서비스 개발에 사용되는 핵심 기술 스택, 라이브러리, 도구와 각 기술의 선정 이유 및 역할을 정의한다.

---

## 1. 핵심 애플리케이션 프레임워크 (Application Core)

| 기술 | 버전 | 역할 | 선정 이유 |
| :--- | :--- | :--- | :--- |
| **Java** | 21 (LTS) | 프로그래밍 언어 | 최신 장기 지원(LTS) 버전으로, 향상된 패턴 매칭, 레코드(Record), 가상 스레드(Virtual Thread) 등 최신 자바 표준 기능 활용 가능 |
| **Spring Boot** | 3.x / 4.x | 백엔드 프레임워크 | 설정 자동화, 임베디드 톰캣, 스프링 생태계(Security, Data JPA 등)와의 완벽한 통합 및 안정적인 운영 환경 제공 |
| **Gradle** | 8.x / 9.x | 빌드 및 의존성 관리 | Groovy/Kotlin DSL 기반의 빠른 빌드 속도(캐싱 및 증분 빌드 지원)와 유연한 멀티 모듈 확장성 |

---

## 2. 데이터베이스 및 영속성 계층 (Database & Persistence)

| 기술 | 버전 / 규격 | 역할 | 선정 이유 |
| :--- | :--- | :--- | :--- |
| **PostgreSQL** | 16+ | 주 관계형 데이터베이스 (RDBMS) | - 표준 SQL을 가장 엄격하게 준수하며 JSONB 등 확장성 우수<br>- **동시성 트랜잭션(MVCC) 및 행 잠금(Row-level Lock)**에 강력하여 공구 선착순 예약 시 재고 초과 방지에 최적 |
| **Spring Data JPA** | - | 데이터 접근 계층 추상화 | 리포지토리 인터페이스 정의만으로 기본 CRUD 쿼리 자동 생성, 객체 지향 도메인 주도 설계(DDD) 구현 용이 |
| **Hibernate (ORM)** | - | JPA 구현체 | 객체(Java Entity)와 테이블 간의 매핑을 자동화하고 변경 감지(Dirty Checking), 캐싱 지원 |
| **H2 Database** | - | 테스트용 인메모리 DB | 로컬 단위 테스트 및 슬라이스 테스트 실행 시 빠른 독립 환경 제공 |

---

## 3. 인증 및 보안 (Security & Authentication)

| 기술 | 역할 | 선정 이유 |
| :--- | :--- | :--- |
| **Spring Security** | 요청 인증 및 접근 제어 | 필터 체인 기반의 표준 보안 프레임워크로 엔드포인트별 인가(RBAC - 구매자/판매자/관리자) 처리 |
| **JWT (JSON Web Token)** | 무상태(Stateless) 세션 관리 | 서버 메모리 부담 없이 Access/Refresh Token을 발급하여 모바일 앱(Capacitor)과 웹 클라이언트 모두에서 확장성 높은 세션 유지 |
| **Kakao OAuth2 Login** | 소셜 로그인 연동 | 별도의 회원가입 복잡도 없이 카카오 고유 식별자를 기반으로 구매자와 판매자를 하나의 통합 계정으로 간편 가입/식별 |

---

## 4. 유효성 검증 및 유틸리티 (Validation & Utilities)

| 기술 | 역할 | 선정 이유 |
| :--- | :--- | :--- |
| **Bean Validation (Jakarta Validation)** | 입력값 형식 검증 | `@NotNull`, `@Min`, `@Pattern` 등의 표준 어노테이션으로 DTO 계층에서 요청 데이터 1차 무결성 검증 |
| **Lombok** | 보일러플레이트 코드 제거 | `@Getter`, `@RequiredArgsConstructor`, `@Builder` 등을 통해 반복적인 생성자, 게터 코드 최소화 |
| **Jackson** | JSON 직렬화 / 역직렬화 | REST API 요청 및 응답 시 객체와 JSON 포맷 간의 표준 변환기 |

---

## 5. 테스트 및 품질 관리 (Test & Quality)

| 기술 | 역할 | 선정 이유 |
| :--- | :--- | :--- |
| **JUnit 5** | 표준 단위 테스트 프레임워크 | 모던 자바 테스트 작성 환경 및 확장 모델 제공 |
| **AssertJ** | 테스트 단언문 라이브러리 | `assertThat(...)` 체이닝 방식으로 가독성 높은 테스트 검증문 작성 |
| **Mockito** | 모킹(Mock) 프레임워크 | 외부 API(카카오 등)나 복잡한 의존 객체를 가짜(Mock) 객체로 대체하여 격리된 단위 테스트 지원 |

---

## 6. 개발 도구 및 인프라 (Tools & Environment)

* **IDE / 에디터:** IntelliJ IDEA / VS Code / Antigravity IDE
* **DB 클라이언트 GUI:** DBeaver / pgAdmin 4
* **API 테스트:** Postman / cURL
* **형상 관리:** Git / GitHub (브랜치 전략: `main`, `develop`, `feat/*`, `docs/*`)
* **이슈 관리:** GitHub Issues & Projects
