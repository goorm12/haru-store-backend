# 하루상점 — 백엔드 문서 안내

- 작성일: 2026-10-20 / 문서 버전: v0.1
- 대상: `haru-store-backend`, 구매자(buyer)·판매자 및 플랫폼 관리자(management)가 함께 사용하는 서버
- 현재 상태: Java 21, Spring Boot 4.1.1, Gradle Wrapper 9.8.1, Spring Web MVC·Bean Validation 및 기본 컨텍스트 테스트만 구성됨. 업무 API·DB·인증·푸시는 구현 전이다.

| 문서 | 용도 |
| --- | --- |
| [백엔드 요구사항](./backend/requirements.md) | 서버 책임, 권한, 범위와 원본 기능·정책 추적 |
| [백엔드 기능 명세](./backend/functional-spec.md) | 인증·예약·수량·상태·알림·탈퇴 처리와 실패 복구 |
| [백엔드 기술 스택](./backend/tech-stack.md) | Java 21, Spring Boot, PostgreSQL, JPA 등 기술 선정 배경과 역할 |

## 기준 문서와 변경 관리

| 기준 | 위치 |
| --- | --- |
| 공통 요구사항·P01~P16 | [buyer 공통 요구사항](https://github.com/goorm12/haru-store-buyer/blob/develop/docs/common/requirements.md) |
| 용어 | [공통 용어](https://github.com/goorm12/haru-store-buyer/blob/develop/docs/common/glossary.md) |
| 공통 인증·시간·수량·상태·탈퇴·QA01~QA28 | [공통 기능 규칙](https://github.com/goorm12/haru-store-buyer/blob/develop/docs/common/functional-rules.md) |
| 공지·문의 C01~C10 | [공지·문의 정책](https://github.com/goorm12/haru-store-buyer/blob/develop/docs/common/communication-policy.md) |
| 구매자 기능 | [buyer 기능 명세](https://github.com/goorm12/haru-store-buyer/blob/develop/docs/buyer/functional-spec.md) |
| 판매자 기능 | [management 판매자 기능 명세](https://github.com/goorm12/haru-store-management/blob/develop/docs/seller/functional-spec.md) |
| 플랫폼 관리자 기능 | [management 관리자 기능 명세](https://github.com/goorm12/haru-store-management/blob/develop/docs/admin/functional-spec.md) |

2026-10-20에 확인한 buyer PR #4·management PR #4 반영 문서를 기준으로 작성했다. 링크의 develop은 이후 변경될 수 있으므로 정책 변경 시 세 저장소의 관련 문서를 함께 검토한다. 공통 정책 원문은 buyer·management의 docs/common에서 관리하며 여기에는 서버의 구현 책임을 정리한다.

P01~P16 및 C01~C10은 기존 문서의 확정 정책이다. 백엔드 요구사항과 기능별 이용 주체·처리 규칙·결과·예외·확인 기준을 정리한다. 공통 명세의 구현 기본안과 후속 설계 사항을 새 사업 정책으로 확정하지 않는다. 문서 작성은 해당 기능의 구현 완료를 의미하지 않는다.

화면·앱 패키징은 buyer, 판매자·관리자 UI는 management, 인증·권한·업무 처리와 데이터 정합성은 backend가 담당한다. 현재는 구현에 앞서 기능 범위와 동작 기준을 정리하는 단계다.
