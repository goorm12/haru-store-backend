# 하루상점 백엔드

구매자 웹·앱과 판매자·플랫폼 관리자 웹이 함께 사용하는 서버입니다.
현재는 기본 환경만 구성되어 있으며 업무 API는 구현 전입니다.

## 기술 구성

| 항목 | 버전·내용 |
| --- | --- |
| Java | 21 LTS |
| Spring Boot | 4.1.1 |
| 빌드 | Gradle Wrapper 9.8.1, Groovy DSL |
| 웹 | Spring Web MVC |
| 입력 검증 | Bean Validation |
| 테스트 | Spring Boot 기본 컨텍스트 로딩 테스트 |

DB·JPA, 카카오 로그인·인증·권한, 예약, 공지·문의, 푸시 알림은 아직 구현하지 않았습니다.

## 시작하기

JDK 21을 설치하고 `JAVA_HOME`을 해당 JDK 폴더로 설정합니다. IDE의 Project SDK와 Gradle JVM도 21로 맞춥니다.
컴파일·테스트는 Java toolchain 21을 사용하며 Gradle은 별도 설치하지 않습니다.

```powershell
git clone https://github.com/goorm12/haru-store-backend.git
cd haru-store-backend
git switch develop
java -version
.\gradlew.bat --version
.\gradlew.bat bootRun
```

초기 구성 PR 병합 전에는 `develop` 대신 `codex/backend-setup` 브랜치를 선택합니다.
기본 포트는 8080이며 아직 컨트롤러가 없으므로 `/` 요청의 404는 정상입니다.
최초 실행 시 의존성 다운로드를 위해 인터넷 연결이 필요합니다.
macOS·Linux에서는 `./gradlew`를 사용합니다.

## 빌드·테스트

```powershell
.\gradlew.bat test
.\gradlew.bat clean build
java -jar build/libs/haru-store-backend-0.0.1-SNAPSHOT.jar
```

- 보고서: `build/reports/tests/test/index.html`
- 실행 JAR: `build/libs/haru-store-backend-0.0.1-SNAPSHOT.jar` (`-plain.jar` 제외)
- 검증: Java 21 clean build 성공, 기본 테스트 1개 통과, 실패·오류 0개.

## 구조

```text
src/main/java/com/harustore/backend/   # 애플리케이션 진입점
src/main/resources/                   # 설정
src/test/java/com/harustore/backend/   # 기본 테스트
gradle/wrapper/                       # Gradle Wrapper
```

## 브랜치·협업

- `main`: 배포 기준
- `develop`: 개발 통합
- `codex/backend-setup`: 초기 구성 작업
- `develop`에서 작업 브랜치를 만들고 PR 대상을 `develop`으로 지정합니다.
- 커밋: `feat : 작업 제목 #이슈번호`, 본문은 `- 작업 내용`으로 한글 작성.
- PR 제목: `[화면 또는 작업 영역] 작업 내용`

최초에는 브랜치 분기를 위한 내용 없는 기준 커밋만 생성하고, 실제 구성은 작업 브랜치에서 PR로 검토합니다.

## 설정

DB·외부 서비스 설정은 해당 기능 도입 시 정의합니다. 비밀번호·API 키·개인 환경 파일은 커밋하지 않습니다.
`.gradle`, `build`, IDE 설정과 `.env`는 Git 추적에서 제외합니다.

## Windows 실행 참고

일부 Windows 앱 실행 환경에서 `Unable to establish loopback connection` 오류가 나면 현재 터미널에 한해 임시 소켓 경로를 지정합니다.

```powershell
New-Item -ItemType Directory -Force .gradle\java-sockets | Out-Null
$env:JAVA_TOOL_OPTIONS = "-Djdk.net.unixdomain.tmpdir=$((Resolve-Path .gradle\java-sockets).Path)"
.\gradlew.bat clean build --no-daemon
```

## 관련 저장소·기획

- [구매자 웹·앱](https://github.com/goorm12/haru-store-buyer)
- [판매자·플랫폼 관리자 웹](https://github.com/goorm12/haru-store-management)
- [공통 요구사항](https://github.com/goorm12/haru-store-buyer/blob/develop/docs/common/requirements.md)
- [공지·문의 정책](https://github.com/goorm12/haru-store-buyer/blob/develop/docs/common/communication-policy.md)

API 경로·DB 모델은 아직 확정하지 않았습니다. 서버 구현 시 위 요구사항과 역할별 소유권 검증을 기준으로 설계합니다.
