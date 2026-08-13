# HearO Backend

## 음성 기록과 보호자 연계를 지원하는 대면 진료 기록 서비스

HearO는 피보호자, 보호자, 의료기관을 연결하고 **대면 진료 현장의 대화와 진료 기록을 관리하는 백엔드 API**입니다.

사용자 유형별 회원 관리와 JWT 인증을 제공하고, 기관 관리자 가입·승인과 소속 기관 사용자 관리, 보호자와 피보호자의 연결 신청·승인, 의료기관 검색과 대면 진료 요청·취소, 진료 내용 기록, 음성 녹음 및 CLOVA Speech 기반 텍스트 변환, AI 진료 요약과 진료 기록 아카이브 조회 흐름을 구현했습니다.

본 저장소는 Java 21과 Spring Boot 기반의 단일 백엔드 애플리케이션이며, MySQL에 서비스 데이터를 저장하고 Redis에 이메일 인증 상태와 Refresh Token을 관리합니다.

---

## 1. 실행 경로 요약

| 실행 경로 | 목적 | 사용 파일/경로 | 접속 주소 |
|---|---|---|---|
| Local Application | 로컬 API 개발 및 기능 검증 | `gradlew`, `src/main/resources/application.properties` | `http://localhost:8081` |
| Test | 도메인·서비스·JWT·메일 기능 검증 | `src/test/java` | Gradle 테스트 리포트 |
| External Services | 데이터 저장, 인증 메일, 음성 인식, 진료 요약 | MySQL, Redis, Gmail SMTP, CLOVA Speech, AI Summary API | 각 서비스 설정에 따름 |

> 기본 애플리케이션 실행에는 MySQL이 필요합니다. Redis는 로그인·토큰 재발급·이메일 인증·비밀번호 변경에, Gmail SMTP와 CLOVA Speech는 각각 인증 메일 발송과 녹음 변환 기능에 필요합니다. 진료 종료 시에는 외부 AI Summary API가 필요합니다.

---

## 2. 주요 기능

| 구분 | 구현 내용 |
|---|---|
| 사용자 관리 | 피보호자, 보호자, 기관 소속 사용자 유형별 회원가입·로그인·아이디 찾기·비밀번호 변경 |
| 기관 관리자 | 기관 계정 가입·로그인·아이디 찾기·비밀번호 변경, 소속 사용자 상태별 페이지 조회와 승인·거절·삭제 |
| 이메일 인증 | Gmail SMTP로 6자리 인증번호 발송, Redis TTL 기반 인증번호 검증 |
| JWT 인증 | 일반 사용자와 기관 관리자 역할별 Access Token·Refresh Token 발급, Bearer Token 검증, 토큰 재발급 |
| 마이페이지 | 사용자 유형별 본인 정보 조회 및 공통 이름 변경 |
| 보호 관계 | 사용자 검색, 연결 신청, 신청 목록 조회, 승인·거절, 메인 보호자 관리, 보호 관계 삭제 |
| 진료 요청 | 피보호자의 의료기관 검색·진료 요청·대기 요청 취소, 의료기관의 요청 조회·거절, 수락과 동시에 진료 시작 |
| 대면 진료 기록 | 기관 사용자 수락 시 아카이브·진료 기록 공간·첫 메시지 생성, 텍스트 기록 조회·저장, 진료 완료 처리 |
| 음성 기록 | 진료 녹음 파일 업로드, CLOVA Speech 연동 코드 기반 음성 인식 및 녹음 기록 저장 |
| AI 진료 요약 | 진료 종료 시 전체 대화를 외부 AI 서비스로 전달하고 주요 증상·의사 소견·기억할 내용·질문 답변·어려운 용어 생성 |
| 진료 아카이브 | 진료 시작 시 아카이브 생성, 종료 시 전체 대화와 AI 요약 저장, 피보호자·보호자별 기록 목록 및 상세 조회 |
| 문의 | 인증 사용자의 문의 등록, 본인 문의 목록·상세 조회 |
| 공통 응답·예외 | `Result` 응답 형식과 전역 예외 처리 적용 |
| 요청값 검증 | 사용자·메일·토큰·이름 변경·채팅 텍스트 요청의 필수값과 이메일 형식 검증 |

---

## 3. 전체 아키텍처

```text
Client
  |
  | HTTP / Bearer JWT
  v
Spring Boot REST API :8081
  |
  +--> User / Care / Medical Treatment / Archive / Inquiry
  |         |
  |         +--------------------------> MySQL :3306
  |
  +--> Email Verification ------------> Redis :6379
  |         |
  |         +--------------------------> Gmail SMTP :587
  |
  +--> Recording Upload --------------> CLOVA Speech API
  |
  +--> Treatment Completion ----------> AI Summary API
```

인증이 필요한 API는 `Authorization: Bearer <ACCESS_TOKEN>` 헤더를 전달합니다. JWT의 `role`은 일반 사용자 계정과 기관 관리자 계정을 구분합니다. 일반 사용자 Resolver는 사용자 ID와 `UserType`을, 기관 관리자 Resolver는 기관 ID를 읽어 각 서비스에 현재 인증 주체를 제공합니다.

---

## 4. 도메인 구성

| 도메인 | 역할 | 주요 구성 |
|---|---|---|
| `user` | 사용자 및 인증 관리 | 피보호자, 보호자, 의료기관, 로그인, 메일 인증, 토큰 재발급 |
| `institution` | 기관 관리자 및 소속 사용자 관리 | 기관 계정 인증, 기관 사용자 상태별 조회·승인·거절·삭제 |
| `ai` | 진료 대화 요약 | 외부 AI 요청, 응답 검증·정규화 |
| `care` | 보호 관계 관리 | 연결 신청, 승인·거절, 연결 대상 검색, 메인 보호자 관리 |
| `medicaltreatment` | 대면 진료 기록 진행 | 의료기관 검색, 진료 요청, 진료 기록 공간, 메시지, 녹음 |
| `archive` | 완료된 진료 기록 관리 | 전체 대화·AI 요약 저장, 사용자별 목록 및 상세 조회 |
| `inquiry` | 사용자 문의 관리 | 문의 등록, 본인 문의 목록·상세 조회 |
| `global` | 공통 기반 기능 | JWT, 공통 응답, 예외 처리, 애플리케이션 설정 |

### 4.1 계정 역할

| 값 | 의미 |
|---|---|
| `USER` | 피보호자·보호자·기관 소속 사용자 계정 |
| `INSTITUTION` | 기관 자체를 관리하는 기관 관리자 계정 |

### 4.2 일반 사용자 유형

| 값 | 의미 |
|---|---|
| `WARD` | 진료를 요청하고 받는 피보호자 |
| `GUARDIAN` | 피보호자의 진료 기록을 확인하는 보호자 |
| `INSTITUTIONS` | 기관에 소속되어 진료 요청을 처리하는 사용자 |

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
| AI Summary | Spring `RestClient`, 외부 진료 요약 API |
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
│   │   ├── ai                   # 외부 AI 진료 요약
│   │   ├── archive              # 진료 아카이브
│   │   ├── care                 # 보호자-피보호자 연결
│   │   ├── global               # JWT, 응답, 예외, 설정
│   │   ├── inquiry              # 사용자 문의
│   │   ├── institution          # 기관 관리자 인증과 소속 사용자 관리
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
| `src/main/java/tohear/hearo/ai` | 외부 AI 요약 요청·응답 검증 |
| `src/main/java/tohear/hearo/care` | 보호 관계 도메인·조회·상태 변경 API |
| `src/main/java/tohear/hearo/medicaltreatment` | 진료 요청, 채팅, 녹음과 의료기관 검색 |
| `src/main/java/tohear/hearo/archive` | 완료 진료 기록 저장 및 조회 |
| `src/main/java/tohear/hearo/inquiry` | 사용자 문의 등록 및 본인 문의 조회 |
| `src/main/java/tohear/hearo/institution` | 기관 관리자 계정과 소속 사용자 승인 상태 관리 |
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
| AI Summary API | 진료 종료 | 전체 진료 대화 요약 |

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
export CLOVA_SPEECH_SECRET='<CLOVA_SPEECH_SECRET>'
export CLOVA_SPEECH_INVOKE_URL='<CLOVA_SPEECH_INVOKE_URL>'
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
| `CLOVA_SPEECH_SECRET` | 녹음 변환 시 | CLOVA Speech Secret Key |
| `CLOVA_SPEECH_INVOKE_URL` | 녹음 변환 시 | CLOVA Speech Invoke URL |

Spring Boot의 외부 설정 우선순위에 따라 위 환경변수가 `application.properties` 값을 덮어씁니다.

> CLOVA Speech 설정은 `CLOVA_SPEECH_SECRET`, `CLOVA_SPEECH_INVOKE_URL` 환경변수로 주입합니다. 두 값이 비어 있으면 녹음 변환 API가 정상 동작하지 않습니다. AI Summary API의 기준 주소는 현재 `AiClientConfig`에 직접 설정되어 있습니다.

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
curl -i http://localhost:8081/api/health
```

정상 상태에서는 `{"status":"UP"}` 응답을 반환합니다.

---

## 9. API 구성

### 9.1 사용자 및 이메일 인증

| Method | Endpoint | 인증 | 설명 |
|---|---|---|---|
| `POST` | `/api/mail/send` | 불필요 | 이메일 인증번호 발송 |
| `POST` | `/api/mail/check` | 불필요 | 이메일 인증번호 확인 |
| `POST` | `/api/users/join` | 불필요 | 이메일 인증 및 중복 확인 후 사용자 유형별 회원가입 |
| `POST` | `/api/users/login` | 불필요 | 로그인 및 토큰 발급 |
| `POST` | `/api/users/find-id` | 불필요 | 이메일로 아이디 찾기 |
| `POST` | `/api/users/to-change-password` | 불필요 | 비밀번호 변경 전 사용자 검증 |
| `POST` | `/api/users/change-password` | 불필요 | 비밀번호 변경 |
| `POST` | `/api/users/token/reissue` | 불필요 | Refresh Token으로 토큰 재발급 |

### 9.2 기관 관리자

| Method | Endpoint | 인증 | 설명 |
|---|---|---|---|
| `GET` | `/api/institutions/search` | 불필요 | 기관명 부분 일치 페이지 검색 |
| `POST` | `/api/institutions/join` | 불필요 | 이메일 인증 후 기관 관리자 계정 가입 |
| `POST` | `/api/institutions/login` | 불필요 | 승인된 기관 관리자 로그인 및 토큰 발급 |
| `POST` | `/api/institutions/id-find` | 불필요 | 기관명과 이메일로 로그인 ID 찾기 |
| `POST` | `/api/institutions/to-change-password` | 불필요 | 비밀번호 재설정 임시 토큰 발급 |
| `POST` | `/api/institutions/change-password` | 불필요 | 임시 토큰으로 기관 비밀번호 변경 |
| `POST` | `/api/institutions/search-pending-user` | 필요 | 소속 승인 대기 사용자 페이지 조회 |
| `POST` | `/api/institutions/search-approved-user` | 필요 | 소속 승인 사용자 페이지 조회 |
| `POST` | `/api/institutions/search-reject-user` | 필요 | 소속 거절 사용자 페이지 조회 |
| `POST` | `/api/institutions/user/approved` | 필요 | 소속 사용자 승인 |
| `POST` | `/api/institutions/user/reject` | 필요 | 소속 사용자 거절 |
| `POST` | `/api/institutions/user/delete` | 필요 | 승인된 소속 사용자를 삭제 상태로 변경 |

소속 사용자 조회와 상태 변경 API에는 기관 관리자용 Access Token이 필요하며, 서버는 토큰에서 기관 ID를 가져와 다른 기관의 사용자에 대한 접근을 차단합니다. 기관 자체가 `APPROVED` 상태일 때만 로그인과 소속 사용자 관리가 가능합니다.

### 9.3 마이페이지

| Method | Endpoint | 인증 | 사용자 유형 | 설명 |
|---|---|---|---|---|
| `GET` | `/api/mypage/ward-user` | 필요 | `WARD` | 피보호자 본인 정보 조회 |
| `GET` | `/api/mypage/guard-user` | 필요 | `GUARDIAN` | 보호자 본인 정보 조회 |
| `GET` | `/api/mypage/institutions-user` | 필요 | `INSTITUTIONS` | 기관 본인 정보 조회 |
| `PATCH` | `/api/mypage/change-name` | 필요 | 전체 사용자 | 로그인 사용자 이름 변경 |

마이페이지 조회는 JWT의 사용자 ID와 유형을 사용합니다. 이름 변경 요청은 사용자 ID나 유형을 받지 않고 `newName`만 받으며, 서비스가 로그인 사용자 유형에 맞는 테이블을 변경합니다.

### 9.4 보호 관계

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
| `POST` | `/api/care/check-main-guard` | 필요 | `WARD` | 승인된 보호자 중 메인 보호자 변경 |
| `POST` | `/api/care/delete-care` | 필요 | `WARD`, `GUARDIAN` | 본인이 당사자인 보호 관계 삭제 |

> `/api/care/user/wards`와 `/api/care/user/Guards`는 현재 코드의 대소문자와 이름을 그대로 표기했습니다.

연결 요청을 처음 승인할 때 아직 메인 보호자가 없으면 해당 보호자가 자동으로 메인 보호자로 지정됩니다. 연결 신청 목록과 연결된 보호자·피보호자 목록 응답에는 `careId`가 포함되며, 연결된 사용자 목록에는 `mainGuardUser`도 포함됩니다. 피보호자와 보호자는 목록에서 받은 `careId`를 이용해 본인이 당사자인 보호 관계를 삭제할 수 있습니다.

### 9.5 피보호자 대면 진료

| Method | Endpoint | 사용자 유형 | 설명 |
|---|---|---|---|
| `GET` | `/api/medical-treatment/ward/institutions?keyword=` | `WARD` | 의료기관 검색 |
| `POST` | `/api/medical-treatment/ward/requests` | `WARD` | 대면 진료 요청 생성 |
| `GET` | `/api/medical-treatment/ward/requests` | `WARD` | 보낸 진료 요청 목록 조회 |
| `GET` | `/api/medical-treatment/ward/requests/{requestId}` | `WARD` | 진료 요청 상세 조회 |
| `POST` | `/api/medical-treatment/ward/requests/{requestId}/cancel` | `WARD` | 본인이 보낸 응답 대기 중 진료 요청 취소 |
| `POST` | `/api/medical-treatment/ward/requests/{requestId}/start` | `WARD` | 생성된 진료 기록 공간 반환, 기존 수락 요청은 공간 생성 후 반환 |
| `GET` | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}` | `WARD` | 진료 기록 공간 조회 |
| `GET` | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages` | `WARD` | 기록 메시지 목록 조회 |
| `POST` | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages` | `WARD` | 텍스트 기록 전송 |
| `POST` | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/complete` | `WARD` | 대면 진료 완료, AI 요약 생성 및 아카이브 저장 |

피보호자는 자신이 보낸 `REQUESTED` 상태의 요청만 취소할 수 있습니다. 취소 API는 요청 행을 잠근 상태에서 소유자와 상태를 확인하고 `CANCELED`로 전환하므로, 기관 사용자의 수락과 동시에 처리되더라도 먼저 완료된 상태 변경만 성공합니다. 이미 수락되었거나 진행 중인 요청은 취소할 수 없습니다.

진료 종료 API는 시스템 메시지를 제외한 대화를 시간순으로 합쳐 AI Summary API에 전달합니다. 응답과 아카이브 상세에는 `mainSymptoms`, `doctorOpinion`, `remember`, `questionAnswer`, `difficultWords`가 포함됩니다. 일부 요약 필드만 비어 있으면 해당 값을 `"없음"`으로 저장하고, 전체 요약이 비어 있거나 외부 서비스 호출이 실패하면 HTTP `502`를 반환합니다.

### 9.6 의료기관 대면 진료

| Method | Endpoint | 사용자 유형 | 설명 |
|---|---|---|---|
| `GET` | `/api/medical-treatment/institution/requests` | `INSTITUTIONS` | 받은 진료 요청 목록 조회 |
| `GET` | `/api/medical-treatment/institution/requests/{requestId}` | `INSTITUTIONS` | 진료 요청 상세 조회 |
| `POST` | `/api/medical-treatment/institution/requests/{requestId}/accept` | `INSTITUTIONS` | 진료 요청 수락, 기록 공간 생성 및 진료 시작 |
| `POST` | `/api/medical-treatment/institution/requests/{requestId}/reject` | `INSTITUTIONS` | 진료 요청 거절 |
| `GET` | `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/messages` | `INSTITUTIONS` | 진료 기록 메시지 목록 조회 |
| `POST` | `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/recordings/complete` | `INSTITUTIONS` | 대면 진료 녹음 변환 및 기록 저장 |

기관 사용자가 진료 요청을 수락하면 요청 잠금과 동일한 트랜잭션 안에서 아카이브, 진료 기록 공간, 첫 시스템 메시지를 생성하고 상태를 `IN_PROGRESS`로 전환합니다. 수락 응답의 `chatRoomId`, `archiveId`를 이용해 즉시 진료 화면에 입장할 수 있으며, 요청 목록·상세 응답에도 생성된 공간의 ID가 포함됩니다. 따라서 진료방 연결 정보는 특정 브라우저의 로컬 저장소가 아닌 서버 응답에서 복구할 수 있습니다. 기존 `start` API는 이미 공간이 있으면 같은 ID를 반환해 중복 생성을 방지하고, 공간이 없는 기존 `ACCEPTED` 요청에 대해서만 생성 로직을 수행합니다.

모든 진료 API는 JWT 인증이 필요합니다. 녹음 완료 API의 Content-Type은 `multipart/form-data`입니다. 코드의 `ChatRoom`, `ChatMessage` 명칭은 네트워크 기반 비대면 채팅이 아니라 대면 진료 과정의 텍스트·음성 기록을 저장하는 내부 도메인 명칭으로 사용합니다.

### 9.7 진료 아카이브

| Method | Endpoint | 사용자 유형 | 설명 |
|---|---|---|---|
| `GET` | `/api/medical-treatment/ward/archives/{archiveId}` | `WARD`, `GUARDIAN` | 접근 가능한 아카이브 상세 조회 |
| `GET` | `/api/medical-treatment/ward/archives/list/for-ward` | `WARD` | 피보호자 본인의 아카이브 목록 조회 |
| `GET` | `/api/medical-treatment/ward/archives/{wardUserId}/list/for-guard` | `GUARDIAN` | 보호자가 피보호자의 아카이브 목록 조회 |

목록 API는 `page`, `size`, `sort` 페이지네이션 파라미터를 사용할 수 있습니다.

아카이브 상세 응답은 `archiveId`, `title`, `archiveDate`, `text`, `allChatText`와 AI 요약 필드 `mainSymptoms`, `doctorOpinion`, `remember`, `questionAnswer`, `difficultWords`를 반환합니다. 보호자는 해당 피보호자와 `APPROVED` 상태의 보호 관계가 있을 때만 목록과 상세를 조회할 수 있습니다.

### 9.8 문의

| Method | Endpoint | 사용자 유형 | 설명 |
|---|---|---|---|
| `POST` | `/api/inquiries` | 인증 사용자 전체 | 문의 등록 |
| `GET` | `/api/inquiries` | 인증 사용자 전체 | 본인 문의 목록 조회 |
| `GET` | `/api/inquiries/{inquiryId}` | 인증 사용자 전체 | 본인 문의 상세 조회 |

문의 등록은 `title`과 `content`를 받으며 제목은 100자, 내용은 5,000자 이하로 제한됩니다. 목록은 생성일시 내림차순으로 반환되고 `page`, `size` 페이지네이션 파라미터를 지원합니다.

### 9.9 공개 기관 검색

| Method | Endpoint | 인증 | 설명 |
|---|---|---|---|
| `GET` | `/api/institutions/search?keyword=` | 불필요 | 기관명 부분 일치 검색 |

기본 페이지는 0, 크기는 10이며 응답은 Spring Data `Page<InstitutionSearchResponse>` 구조입니다.

### 9.10 주요 요청 데이터

| API | 요청 필드 | 설명 |
|---|---|---|
| 이메일 인증번호 발송 | `email` | 인증번호를 받을 이메일 |
| 이메일 인증번호 확인 | `email`, `checkNumber` | 이메일과 6자리 인증번호 |
| 회원가입 | `id`, `name`, `email`, `password`, `checkPassword`, `userType` | 이메일 인증 완료 후 사용자 유형별 계정 생성 |
| 로그인 | `id`, `password` | 로그인 정보 |
| 아이디 찾기 | `name`, `email` | 이름과 이메일로 아이디 조회 |
| 비밀번호 변경 사전 인증 | `name`, `email` | 이메일 인증 상태와 사용자 확인 |
| 비밀번호 변경 | `id`, `newPassword`, `checkNewPassword`, `userType`, `tempToken` | 새 비밀번호와 임시 토큰 |
| 토큰 재발급 | `refreshToken` | 로그인 시 발급된 Refresh Token |
| 기관 가입 | `institutionName`, `email`, `institutionId`, `password`, `checkPassword` | 이메일 인증 후 기관 관리자 계정 생성 |
| 기관 로그인 | `loginId`, `password` | 승인된 기관 관리자 로그인 |
| 기관 아이디 찾기 | `institutionName`, `email` | 기관명과 이메일로 로그인 ID 조회 |
| 기관 비밀번호 변경 사전 인증 | `institutionName`, `email` | 이메일 인증 상태와 기관 정보 확인 |
| 기관 비밀번호 변경 | `institutionId`, `newPassword`, `checkNewPassword`, `tempToken` | 기관 비밀번호 재설정 |
| 기관 사용자 상태 변경 | `institutionsUserId` | 승인·거절·삭제할 소속 사용자 ID |
| 이름 변경 | `newName` | 로그인 사용자의 새 이름 |
| 보호 관계 신청 | `wardUserId` | 연결할 피보호자 ID |
| 보호 관계 승인·거절 | `careId` | 상태를 변경할 연결 ID |
| 메인 보호자 변경 | `changeGuardUserId` | 새 메인 보호자로 지정할 승인된 보호자 ID |
| 보호 관계 삭제 | `deleteCareId` | 삭제할 보호 관계의 Care ID |
| 진료 요청 | `institutionUserId` | 진료를 요청할 의료기관 ID |
| 텍스트 기록 | `content` | 대면 진료 중 저장할 텍스트 |
| 녹음 완료 | `file` | `multipart/form-data` 오디오 파일 |
| 문의 등록 | `title`, `content` | 제목과 문의 내용 |

`userType`에는 코드에 정의된 `WARD`, `GUARDIAN`, `INSTITUTIONS` 중 하나를 사용합니다.

> 회원가입 시 Redis의 이메일 인증 완료 상태를 확인하고 세 사용자 테이블 전체에서 이메일 중복을 검사합니다. 가입 성공 후 해당 이메일의 인증 완료 상태는 삭제됩니다.

사용자 인증·메일·토큰·이름 변경·채팅 텍스트 요청에는 Jakarta Bean Validation을 적용합니다. 필수 문자열은 공백만 전달해도 거부하며, 회원가입 아이디는 5자 이상이어야 하고 이메일 입력값은 이메일 형식이어야 합니다. 검증 실패는 HTTP 400과 공통 `Result` 형식으로 반환됩니다.

---

## 10. 인증 및 공통 응답

### 10.1 인증 헤더

```http
Authorization: Bearer <ACCESS_TOKEN>
```

Access Token 기본 유효기간은 2일, Refresh Token 기본 유효기간은 14일입니다.

JWT의 `role` 클레임은 `USER` 또는 `INSTITUTION`입니다. `USER` 토큰에는 `userType`이 추가되며, `INSTITUTION` 토큰의 subject에는 기관 ID가 저장됩니다. Refresh Token은 충돌을 피하기 위해 Redis에 역할별 키로 저장됩니다.

```text
refresh-token:user:{userId}
refresh-token:institution:{institutionId}
```

### 10.2 공통 응답 형식

```json
{
  "status": "200",
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

잘못된 요청과 인증 실패는 전역 예외 처리기를 통해 각각 HTTP `400`, `401` 응답으로 변환됩니다. 음성 인식·파일 저장·AI 요약 외부 서비스 오류는 HTTP `502`로 변환됩니다.

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

### 11.6 대면 진료 요청 취소

대기 중인 진료 요청 취소:

```bash
curl -X POST http://localhost:8081/api/medical-treatment/ward/requests/1/cancel \
  -H 'Authorization: Bearer <ACCESS_TOKEN>'
```

### 11.7 대면 진료 텍스트 기록

```bash
curl -X POST http://localhost:8081/api/medical-treatment/ward/chat-rooms/1/messages \
  -H 'Authorization: Bearer <ACCESS_TOKEN>' \
  -H 'Content-Type: application/json' \
  -d '{"content":"대면 진료 중 기록할 내용"}'
```

### 11.8 녹음 파일 변환

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
- 일반 사용자 Refresh Token은 `refresh-token:user:<userId>` 키로 저장됩니다.
- 기관 관리자 Refresh Token은 `refresh-token:institution:<institutionId>` 키로 저장됩니다.
- 일반 사용자 회원가입 성공 시 사용한 `mail-verified:<email>` 키는 즉시 삭제됩니다.

### JWT 인증 실패

- 요청 헤더가 `Bearer ` 접두사를 포함하는지 확인합니다.
- Access Token 만료 여부와 서버의 `JWT_SECRET`이 발급 당시 값과 같은지 확인합니다.

### 이메일이 발송되지 않음

- `MAIL_PASSWORD`에 일반 계정 비밀번호가 아닌 Gmail 앱 비밀번호를 설정합니다.
- Gmail 계정의 2단계 인증과 SMTP 사용 가능 여부를 확인합니다.

### 녹음 변환 실패

- `ClovaSpeechClient`에 CLOVA Speech Secret Key와 Invoke URL이 설정되었는지 확인합니다.
- 요청이 `multipart/form-data`이고 오디오 파일이 포함되었는지 확인합니다.

### AI 요약 실패

- `AiClientConfig`의 AI Summary API 기준 주소와 외부 서비스 상태를 확인합니다.
- 전체 요약 필드가 비어 있는 응답은 실패로 처리됩니다.
- 일부 필드만 비어 있으면 해당 필드는 `"없음"`으로 정규화됩니다.

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

HearO는 사용자 유형별 권한과 보호 관계를 중심으로 대면 진료 요청부터 현장 대화 기록, 음성 텍스트 변환, AI 진료 요약, 완료된 진료 기록 조회까지 하나의 흐름으로 연결합니다.

Strategy 형태의 사용자 서비스 분기, 커스텀 JWT 사용자 주입, QueryDSL 기반 조회, Redis TTL 이메일 인증, 비관적 잠금을 적용한 진료 요청 상태 전이, 외부 Speech-to-Text·AI Summary 연동을 통해 실제 서비스 백엔드에서 필요한 인증·영속성·동시성·외부 API 통합을 함께 다룹니다.

---

## Web CI/CD

백엔드 배포는 GitHub Actions, Amazon ECR, Kubernetes, Argo CD를 이용한 GitOps 방식으로 자동화되어 있습니다.

```text
main 브랜치 Push 또는 수동 실행
  |
  v
Gradle Test (MySQL 8.4 + Redis 7.4)
  |
  v
Docker Image Build
  |
  v
Amazon ECR Push (Commit SHA Tag)
  |
  v
k8s/hearo-backend.yaml 이미지 갱신 및 Commit
  |
  v
Argo CD가 변경 감지 후 Kubernetes 배포
```

| 단계 | 동작 |
|---|---|
| Trigger | `main` 브랜치 Push 또는 GitHub Actions의 수동 실행(`workflow_dispatch`) |
| Test | GitHub Actions Service Container로 MySQL과 Redis를 실행한 뒤 `./gradlew clean test --no-daemon` 수행 |
| Build | 멀티 스테이지 `Dockerfile`로 Java 21 애플리케이션 이미지 빌드 |
| Push | GitHub OIDC로 AWS IAM Role을 위임받아 `hearo-backend` ECR 저장소에 이미지 Push |
| Tag | 배포 이미지를 Git Commit SHA로 태깅하여 버전 추적 가능 |
| Manifest Update | `k8s/hearo-backend.yaml`의 이미지 주소를 새 태그로 변경하고 `main`에 자동 Commit |
| Deploy | Argo CD가 Git 변경 사항을 감지하여 Kubernetes Deployment와 Service에 반영 |

워크플로우 파일은 `.github/workflows/cicd.yml`, 컨테이너 빌드 설정은 `Dockerfile`, 배포 매니페스트는 `k8s/hearo-backend.yaml`에서 확인할 수 있습니다. `k8s/**`와 Markdown 파일만 변경한 Push는 파이프라인을 실행하지 않으며, 자동 매니페스트 Commit에는 `[skip ci]`가 포함되어 중복 실행을 방지합니다.

### GitHub Actions 설정값

| 유형 | 이름 | 설명 |
|---|---|---|
| Variable | `AWS_REGION` | ECR이 위치한 AWS 리전 |
| Variable | `AWS_ACCOUNT_ID` | AWS 계정 ID |
| Secret | `AWS_ROLE_ARN` | GitHub Actions가 OIDC로 Assume할 IAM Role ARN |

AWS IAM Role에는 ECR 이미지 Push 권한이 필요하며, GitHub Actions에는 Kubernetes 매니페스트를 갱신하기 위한 `contents: write` 권한이 설정되어 있습니다. 운영 환경의 DB, 메일, JWT, CLOVA Speech 설정은 Git에 저장하지 않고 `hearo-backend-secret` Kubernetes Secret으로 주입합니다.
Strategy 형태의 사용자 서비스 분기, 커스텀 JWT 사용자 주입, QueryDSL 기반 조회, Redis TTL 이메일 인증, 외부 Speech-to-Text·AI Summary 연동을 통해 실제 서비스 백엔드에서 필요한 인증·영속성·외부 API 통합을 함께 다룹니다.
