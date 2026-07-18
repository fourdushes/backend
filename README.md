# HearO Backend

## 음성 기록과 보호자 연계를 지원하는 대면 진료 기록 서비스

HearO는 피보호자, 보호자, 의료기관을 연결하고 **대면 진료 현장의 대화와 진료 기록을 관리하는 백엔드 API**입니다.

사용자 유형별 회원 관리와 JWT 인증을 제공하고, 보호자와 피보호자의 연결 신청·승인, 의료기관 검색과 대면 진료 요청, 진료 내용 기록, 음성 녹음 및 CLOVA Speech 기반 텍스트 변환, 진료 기록 아카이브 조회 흐름을 구현했습니다.

본 저장소는 Java 21과 Spring Boot 기반의 단일 백엔드 애플리케이션이며, MySQL에 서비스 데이터를 저장하고 Redis에 이메일 인증 상태를 관리합니다.

---

## 1. 실행 경로 요약

| 실행 경로 | 목적 | 사용 파일/경로 | 접속 주소 |
|---|---|---|---|
| Local Application | 로컬 API 개발 및 기능 검증 | `gradlew`, `src/main/resources/application.properties` | `http://localhost:8081` |
| Test | 도메인·서비스·JWT·메일 기능 검증 | `src/test/java` | Gradle 테스트 리포트 |
| External Services | 데이터 저장, 인증 메일, 음성 인식 | MySQL, Redis, Gmail SMTP, CLOVA Speech | 각 서비스 설정에 따름 |

> 기본 애플리케이션 실행에는 MySQL이 필요합니다. Redis는 로그인·토큰 재발급·이메일 인증·비밀번호 변경에, Gmail SMTP와 CLOVA Speech는 각각 인증 메일 발송과 녹음 변환 기능에 필요합니다.

---

## 2. 주요 기능

| 구분 | 구현 내용 |
|---|---|
| 사용자 관리 | 피보호자, 보호자, 의료기관 유형별 회원가입·로그인·아이디 찾기·비밀번호 변경 |
| 이메일 인증 | Gmail SMTP로 6자리 인증번호 발송, Redis TTL 기반 인증번호 검증 |
| JWT 인증 | Access Token·Refresh Token 발급, Bearer Token 검증, 토큰 재발급 |
| 보호 관계 | 사용자 검색, 연결 신청, 신청 목록 조회, 승인·거절 |
| 진료 요청 | 피보호자의 의료기관 검색·진료 요청, 의료기관의 요청 조회·수락·거절 |
| 대면 진료 기록 | 진료 기록 공간 생성, 텍스트 기록 조회·저장, 진료 완료 처리 |
| 음성 기록 | 진료 녹음 파일 업로드, CLOVA Speech 연동 코드 기반 음성 인식 및 녹음 기록 저장 |
| 진료 아카이브 | 진료 종료 시 아카이브 저장, 피보호자·보호자별 기록 목록 및 상세 조회 |
| 공통 응답·예외 | `Result` 응답 형식과 전역 예외 처리 적용 |

---

## 3. 전체 아키텍처

```text
Client
  |
  | HTTP / Bearer JWT
  v
Spring Boot REST API :8081
  |
  +--> User / Care / Medical Treatment / Archive
  |         |
  |         +--------------------------> MySQL :3306
  |
  +--> Email Verification ------------> Redis :6379
  |         |
  |         +--------------------------> Gmail SMTP :587
  |
  +--> Recording Upload --------------> CLOVA Speech API
```

인증이 필요한 API는 `Authorization: Bearer <ACCESS_TOKEN>` 헤더를 전달합니다. 커스텀 Argument Resolver가 토큰에서 사용자 ID와 유형을 읽어 각 서비스에 현재 사용자 정보를 제공합니다.

---

## 4. 도메인 구성

| 도메인 | 역할 | 주요 구성 |
|---|---|---|
| `user` | 사용자 및 인증 관리 | 피보호자, 보호자, 의료기관, 로그인, 메일 인증, 토큰 재발급 |
| `care` | 보호 관계 관리 | 연결 신청, 승인·거절, 연결 대상 검색 |
| `medicaltreatment` | 대면 진료 기록 진행 | 의료기관 검색, 진료 요청, 진료 기록 공간, 메시지, 녹음 |
| `archive` | 완료된 진료 기록 관리 | 진료 내용 저장, 사용자별 목록 및 상세 조회 |
| `global` | 공통 기반 기능 | JWT, 공통 응답, 예외 처리, 애플리케이션 설정 |

### 4.1 사용자 유형

| 값 | 의미 |
|---|---|
| `WARD` | 진료를 요청하고 받는 피보호자 |
| `GUARDIAN` | 피보호자의 진료 기록을 확인하는 보호자 |
| `INSTITUTIONS` | 진료 요청을 처리하는 의료기관 |

---

## 5. 기술 스택

| 영역 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6, Spring MVC |
| Persistence | Spring Data JPA, QueryDSL 5.0.0, Hibernate |
| Database | MySQL |
| Cache | Redis |
| Authentication | JWT (`jjwt` 0.11.5), Spring Security Crypto |
| Mail | Spring Mail, Gmail SMTP |
| Speech-to-Text | NAVER Cloud CLOVA Speech |
| Build / Test | Gradle Wrapper, JUnit Platform |
| Utilities | Lombok, Gson, Apache HttpClient |

---

## 6. 포트 구성

| 항목 | 포트 | 주소 | 설명 |
|---|---:|---|---|
| HearO API | `8081` | `http://localhost:8081` | Spring Boot REST API |
| MySQL | `3306` | `localhost:3306/hearo` | 영속 데이터 저장 |
| Redis | `6379` | `localhost:6379` | 이메일 인증번호 및 인증 상태 저장 |
| Gmail SMTP | `587` | `smtp.gmail.com:587` | STARTTLS 기반 인증 메일 발송 |

---

## 7. 디렉터리 구조

```text
src
├── main
│   ├── java/tohear/hearo
│   │   ├── archive              # 진료 아카이브
│   │   ├── care                 # 보호자-피보호자 연결
│   │   ├── global               # JWT, 응답, 예외, 설정
│   │   ├── medicaltreatment     # 진료 요청, 채팅, 녹음
│   │   ├── user                 # 사용자, 인증, 메일
│   │   └── HearoApplication.java
│   └── resources
│       └── application.properties
└── test/java/tohear/hearo        # 단위 및 애플리케이션 테스트
```

| 경로 | 역할 |
|---|---|
| `src/main/java/tohear/hearo/user` | 사용자 유형별 엔티티·서비스와 인증 API |
| `src/main/java/tohear/hearo/care` | 보호 관계 도메인·조회·상태 변경 API |
| `src/main/java/tohear/hearo/medicaltreatment` | 진료 요청, 채팅, 녹음과 의료기관 검색 |
| `src/main/java/tohear/hearo/archive` | 완료 진료 기록 저장 및 조회 |
| `src/main/java/tohear/hearo/global` | 공통 인증·응답·예외 처리 |
| `src/test/java/tohear/hearo` | 주요 도메인과 서비스 테스트 |

---

## 8. 로컬 실행

### 8.1 사전 요구사항

| 항목 | 필수 시점 | 용도 |
|---|---|---|
| Java 21 | 빌드 및 실행 | Spring Boot 애플리케이션 실행 |
| MySQL 8.x | 기본 실행 | 사용자, 연결, 진료, 기록 데이터 저장 |
| Redis | 인증 기능 사용 | Refresh Token, 이메일 인증, 비밀번호 재설정 토큰 저장 |
| Gmail 앱 비밀번호 | 인증 메일 발송 | Gmail SMTP 인증번호 발송 |
| CLOVA Speech Key·Invoke URL | 녹음 변환 | 대면 진료 녹음 파일의 텍스트 변환 |

### 8.2 데이터베이스 생성

```sql
CREATE DATABASE hearo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

기본 연결 주소는 `jdbc:mysql://127.0.0.1:3306/hearo`이며, Hibernate가 `spring.jpa.hibernate.ddl-auto=update` 설정으로 테이블을 갱신합니다.

### 8.3 환경변수 설정

민감정보는 Git에 추가하지 말고 실행 환경에서 주입합니다.

```bash
export MAIL_PASSWORD='<GMAIL_APP_PASSWORD>'
export SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/hearo'
export SPRING_DATASOURCE_USERNAME='<MYSQL_USERNAME>'
export SPRING_DATASOURCE_PASSWORD='<MYSQL_PASSWORD>'
export JWT_SECRET='<BASE64_ENCODED_SECRET>'
export SPRING_DATA_REDIS_HOST='localhost'
export SPRING_DATA_REDIS_PORT='6379'
```

| 환경변수 | 필수 여부 | 설명 |
|---|---|---|
| `SPRING_DATASOURCE_URL` | 필수 | MySQL JDBC 연결 주소 |
| `SPRING_DATASOURCE_USERNAME` | 필수 | MySQL 사용자명 |
| `SPRING_DATASOURCE_PASSWORD` | 필수 | MySQL 비밀번호 |
| `JWT_SECRET` | 필수 | Access·Refresh Token 서명 키 |
| `SPRING_DATA_REDIS_HOST` | 인증 기능 사용 시 | Redis 호스트 |
| `SPRING_DATA_REDIS_PORT` | 인증 기능 사용 시 | Redis 포트 |
| `MAIL_PASSWORD` | 메일 발송 시 | Gmail 앱 비밀번호 |

Spring Boot의 외부 설정 우선순위에 따라 위 환경변수가 `application.properties` 값을 덮어씁니다.

> CLOVA Speech의 `SECRET`, `INVOKE_URL`은 현재 환경변수로 연결되어 있지 않고 `ClovaSpeechClient` 내부 값이 비어 있습니다. 따라서 연동 코드 자체는 구현되어 있지만, 해당 값을 별도로 연결하기 전에는 녹음 변환 API가 정상 동작하지 않습니다.

### 8.4 애플리케이션 실행

macOS / Linux:

```bash
./gradlew bootRun
```

Windows:

```powershell
.\gradlew.bat bootRun
```

실행 후 API 기본 주소:

```text
http://localhost:8081
```

기본 상태 확인:

```bash
curl -i http://localhost:8081
```

루트 전용 API는 구현되어 있지 않으므로 HTTP `404`가 반환되더라도 서버 응답이 도착하면 애플리케이션이 실행 중인 것입니다.

---

## 9. API 구성

### 9.1 사용자 및 이메일 인증

| Method | Endpoint | 인증 | 설명 |
|---|---|---|---|
| `POST` | `/api/mail/send` | 불필요 | 이메일 인증번호 발송 |
| `POST` | `/api/mail/check` | 불필요 | 이메일 인증번호 확인 |
| `POST` | `/api/users/join` | 불필요 | 사용자 유형별 회원가입 |
| `POST` | `/api/users/login` | 불필요 | 로그인 및 토큰 발급 |
| `POST` | `/api/users/find-id` | 불필요 | 이메일로 아이디 찾기 |
| `POST` | `/api/users/to-change-password` | 불필요 | 비밀번호 변경 전 사용자 검증 |
| `POST` | `/api/users/change-password` | 불필요 | 비밀번호 변경 |
| `POST` | `/api/users/token/reissue` | 불필요 | Refresh Token으로 토큰 재발급 |

### 9.2 보호 관계

| Method | Endpoint | 인증 | 사용자 유형 | 설명 |
|---|---|---|---|---|
| `GET` | `/api/care/user/search-ward-user` | 필요 | `GUARDIAN` | 연결할 피보호자 검색 |
| `POST` | `/api/care/user/save-care` | 필요 | `GUARDIAN` | 보호 관계 연결 신청 |
| `GET` | `/api/care/user/ward/check-care-list` | 필요 | `WARD` | 피보호자의 연결 신청 목록 조회 |
| `GET` | `/api/care/user/guard/check-care-list` | 필요 | `GUARDIAN` | 보호자의 연결 신청 목록 조회 |
| `POST` | `/api/care/user/change-care-approve` | 필요 | `WARD` | 연결 신청 승인 |
| `POST` | `/api/care/user/change-care-reject` | 필요 | `WARD` | 연결 신청 거절 |
| `GET` | `/api/care/user/wards` | 필요 | `WARD` | 연결된 보호자 조회 |
| `GET` | `/api/care/user/Guards` | 필요 | `GUARDIAN` | 연결된 피보호자 조회 |

> 마지막 두 경로는 현재 코드의 대소문자와 이름을 그대로 표기했습니다.

### 9.3 피보호자 대면 진료

| Method | Endpoint | 사용자 유형 | 설명 |
|---|---|---|---|
| `GET` | `/api/medical-treatment/ward/institutions?keyword=` | `WARD` | 의료기관 검색 |
| `POST` | `/api/medical-treatment/ward/requests` | `WARD` | 대면 진료 요청 생성 |
| `GET` | `/api/medical-treatment/ward/requests` | `WARD` | 보낸 진료 요청 목록 조회 |
| `GET` | `/api/medical-treatment/ward/requests/{requestId}` | `WARD` | 진료 요청 상세 조회 |
| `POST` | `/api/medical-treatment/ward/requests/{requestId}/start` | `WARD` | 수락된 대면 진료의 기록 공간 생성 |
| `GET` | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}` | `WARD` | 진료 기록 공간 조회 |
| `GET` | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages` | `WARD` | 기록 메시지 목록 조회 |
| `POST` | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages` | `WARD` | 텍스트 기록 전송 |
| `POST` | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/complete` | `WARD` | 대면 진료 완료 및 아카이브 생성 |

### 9.4 의료기관 대면 진료

| Method | Endpoint | 사용자 유형 | 설명 |
|---|---|---|---|
| `GET` | `/api/medical-treatment/institution/requests` | `INSTITUTIONS` | 받은 진료 요청 목록 조회 |
| `GET` | `/api/medical-treatment/institution/requests/{requestId}` | `INSTITUTIONS` | 진료 요청 상세 조회 |
| `POST` | `/api/medical-treatment/institution/requests/{requestId}/accept` | `INSTITUTIONS` | 진료 요청 수락 |
| `POST` | `/api/medical-treatment/institution/requests/{requestId}/reject` | `INSTITUTIONS` | 진료 요청 거절 |
| `GET` | `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/messages` | `INSTITUTIONS` | 진료 기록 메시지 목록 조회 |
| `POST` | `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/recordings/complete` | `INSTITUTIONS` | 대면 진료 녹음 변환 및 기록 저장 |

모든 진료 API는 JWT 인증이 필요합니다. 녹음 완료 API의 Content-Type은 `multipart/form-data`입니다. 코드의 `ChatRoom`, `ChatMessage` 명칭은 네트워크 기반 비대면 채팅이 아니라 대면 진료 과정의 텍스트·음성 기록을 저장하는 내부 도메인 명칭으로 사용합니다.

### 9.5 진료 아카이브

| Method | Endpoint | 사용자 유형 | 설명 |
|---|---|---|---|
| `GET` | `/api/medical-treatment/ward/archives/{archiveId}` | `WARD`, `GUARDIAN` | 접근 가능한 아카이브 상세 조회 |
| `GET` | `/api/medical-treatment/ward/archives/list/for-ward` | `WARD` | 피보호자 본인의 아카이브 목록 조회 |
| `GET` | `/api/medical-treatment/ward/archives/{wardUserId}/list/for-guard` | `GUARDIAN` | 보호자가 피보호자의 아카이브 목록 조회 |

목록 API는 `page`, `size`, `sort` 페이지네이션 파라미터를 사용할 수 있습니다.

> 현재 아카이브 접근 검증은 연결 관계의 존재 여부를 확인합니다. 서비스 코드에서 `CareState.APPROVED` 상태를 별도로 검사하지 않는 현재 동작을 기준으로 문서화했습니다.

### 9.6 주요 요청 데이터

| API | 요청 필드 | 설명 |
|---|---|---|
| 이메일 인증번호 발송 | `email` | 인증번호를 받을 이메일 |
| 이메일 인증번호 확인 | `email`, `checkNumber` | 이메일과 6자리 인증번호 |
| 회원가입 | `id`, `name`, `email`, `password`, `userType` | 사용자 유형별 계정 생성 |
| 로그인 | `id`, `password` | 로그인 정보 |
| 아이디 찾기 | `name`, `email` | 이름과 이메일로 아이디 조회 |
| 비밀번호 변경 사전 인증 | `name`, `email` | 이메일 인증 상태와 사용자 확인 |
| 비밀번호 변경 | `id`, `newPassword`, `checkNewPassword`, `userType`, `tempToken` | 새 비밀번호와 임시 토큰 |
| 토큰 재발급 | `refreshToken` | 로그인 시 발급된 Refresh Token |
| 보호 관계 신청 | `wardUserId` | 연결할 피보호자 ID |
| 보호 관계 승인·거절 | `careId` | 상태를 변경할 연결 ID |
| 진료 요청 | `institutionUserId` | 진료를 요청할 의료기관 ID |
| 텍스트 기록 | `content` | 대면 진료 중 저장할 텍스트 |
| 녹음 완료 | `file` | `multipart/form-data` 오디오 파일 |

`userType`에는 코드에 정의된 `WARD`, `GUARDIAN`, `INSTITUTIONS` 중 하나를 사용합니다.

> 현재 회원가입 로직은 이메일 인증 완료 여부를 검사하지 않습니다. 이메일 인증 완료 상태는 비밀번호 변경 사전 인증 과정에서 확인합니다.

---

## 10. 인증 및 공통 응답

### 10.1 인증 헤더

```http
Authorization: Bearer <ACCESS_TOKEN>
```

Access Token 기본 유효기간은 2일, Refresh Token 기본 유효기간은 14일입니다.

### 10.2 공통 응답 형식

```json
{
  "status": "200",
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

잘못된 요청과 인증 실패는 전역 예외 처리기를 통해 각각 HTTP `400`, `401` 응답으로 변환됩니다.

---

## 11. API 검증 예시

### 11.1 이메일 인증번호 발송

```bash
curl -X POST http://localhost:8081/api/mail/send \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@example.com"}'
```

### 11.2 로그인

```bash
curl -X POST http://localhost:8081/api/users/login \
  -H 'Content-Type: application/json' \
  -d '{"id":"ward01","password":"password"}'
```

### 11.3 회원가입

```bash
curl -X POST http://localhost:8081/api/users/join \
  -H 'Content-Type: application/json' \
  -d '{
    "id":"ward01",
    "name":"홍길동",
    "email":"user@example.com",
    "password":"password",
    "userType":"WARD"
  }'
```

### 11.4 의료기관 검색

```bash
curl 'http://localhost:8081/api/medical-treatment/ward/institutions?keyword=병원' \
  -H 'Authorization: Bearer <ACCESS_TOKEN>'
```

### 11.5 대면 진료 요청

```bash
curl -X POST http://localhost:8081/api/medical-treatment/ward/requests \
  -H 'Authorization: Bearer <ACCESS_TOKEN>' \
  -H 'Content-Type: application/json' \
  -d '{"institutionUserId":"institution01"}'
```

### 11.6 대면 진료 텍스트 기록

```bash
curl -X POST http://localhost:8081/api/medical-treatment/ward/chat-rooms/1/messages \
  -H 'Authorization: Bearer <ACCESS_TOKEN>' \
  -H 'Content-Type: application/json' \
  -d '{"content":"대면 진료 중 기록할 내용"}'
```

### 11.7 녹음 파일 변환

```bash
curl -X POST http://localhost:8081/api/medical-treatment/institution/chat-rooms/1/recordings/complete \
  -H 'Authorization: Bearer <ACCESS_TOKEN>' \
  -F 'file=@./recording.webm'
```

이 요청은 CLOVA Speech Key와 Invoke URL이 연결된 환경에서만 정상 동작합니다.

---

## 12. 테스트 및 빌드

주요 도메인과 일부 서비스·JWT·메일 로직 테스트:

```bash
./gradlew test
```

빌드:

```bash
./gradlew clean build
```

테스트 결과는 `build/reports/tests/test/index.html`에서 확인할 수 있습니다.

---

## 13. Troubleshooting

### MySQL 연결 실패

- MySQL이 `3306` 포트에서 실행 중인지 확인합니다.
- `hearo` 데이터베이스가 생성되었는지 확인합니다.
- `SPRING_DATASOURCE_*` 환경변수의 URL, 사용자명, 비밀번호를 확인합니다.

### Redis 연결 실패

- Redis가 `6379` 포트에서 실행 중인지 확인합니다.
- 이메일 인증번호는 `mail:<email>` 키로 3분간 저장됩니다.
- 인증 성공 상태는 `mail-verified:<email>` 키로 10분간 유지됩니다.

### JWT 인증 실패

- 요청 헤더가 `Bearer ` 접두사를 포함하는지 확인합니다.
- Access Token 만료 여부와 서버의 `JWT_SECRET`이 발급 당시 값과 같은지 확인합니다.

### 이메일이 발송되지 않음

- `MAIL_PASSWORD`에 일반 계정 비밀번호가 아닌 Gmail 앱 비밀번호를 설정합니다.
- Gmail 계정의 2단계 인증과 SMTP 사용 가능 여부를 확인합니다.

### 녹음 변환 실패

- `ClovaSpeechClient`에 CLOVA Speech Secret Key와 Invoke URL이 설정되었는지 확인합니다.
- 요청이 `multipart/form-data`이고 오디오 파일이 포함되었는지 확인합니다.

---

## 14. 보안 및 환경변수 관리

- DB 비밀번호, JWT Secret, 메일 앱 비밀번호, CLOVA Speech Key를 저장소에 커밋하지 않습니다.
- 운영 환경에서는 모든 민감 설정을 환경변수 또는 Secret Manager로 주입합니다.
- 충분히 긴 Base64 인코딩 키를 JWT Secret으로 사용하고 환경별로 분리합니다.
- 로그와 API 응답에 토큰, 비밀번호, 인증번호가 노출되지 않도록 관리합니다.
- 현재 개발용 `application.properties`에는 DB 비밀번호와 JWT Secret 기본값이 포함되어 있으므로 공개 또는 배포 전에 반드시 제거하고 환경변수로 이전해야 합니다.
- 이미 Git 이력에 포함된 키와 비밀번호는 즉시 폐기·재발급한 뒤 이력 정리를 검토합니다.

---

## 15. 프로젝트 의의

HearO는 사용자 유형별 권한과 보호 관계를 중심으로 대면 진료 요청부터 현장 대화 기록, 음성 텍스트 변환, 완료된 진료 기록 조회까지 하나의 흐름으로 연결합니다.

Strategy 형태의 사용자 서비스 분기, 커스텀 JWT 사용자 주입, QueryDSL 기반 조회, Redis TTL 이메일 인증, 외부 Speech-to-Text 연동을 통해 실제 서비스 백엔드에서 필요한 인증·영속성·외부 API 통합을 함께 다룹니다.
