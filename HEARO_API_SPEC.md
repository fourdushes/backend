# HearO API 명세서

> 작성 기준: 현재 저장소의 Spring Boot 운영 코드
> 스캔 일자: 2026-07-31
> 담당자: 양준형
> 공통 응답: 별도 표기가 없으면 HTTP 200과 `Result<T>` JSON을 반환한다.

## 0. GPT 작업 명령문

아래 명령문은 이 파일과 기존 Notion API 명세서를 함께 제공한 뒤 GPT에게 그대로 전달한다.

```text
현재 제공된 HEARO_API_SPEC.md를 기준 문서로 사용하여 기존 Notion의 `API 명세서` 데이터베이스와 각 API의 `엔드포인트 상세 (작성 템플릿)` 페이지를 수정하라.

[작업 범위]
1. 먼저 현재 Notion의 `API 명세서` 데이터베이스와 `엔드포인트 상세 (작성 템플릿)` 구조를 읽고 속성명, 열 순서, 상세 페이지 구성을 확인한다.
2. HEARO_API_SPEC.md의 API 인덱스 56개를 Notion API 명세서와 `HTTP Method + API Path` 기준으로 비교한다.
3. 동일한 API가 있으면 중복 생성하지 말고 기존 행과 상세 페이지를 수정한다.
4. API가 없으면 기존 템플릿을 복제하여 새 행과 상세 페이지를 생성한다.
5. Notion에만 있고 현재 명세 파일에 없는 API는 삭제하지 말고 `코드에서 확인되지 않음`으로 표시한 뒤 최종 보고에 포함한다.
6. 각 상세 페이지에는 Method, Path, Auth, Description, Request, Response, 응답 예시, Error Codes를 HEARO_API_SPEC.md 내용과 일치하도록 작성한다.
7. 담당자는 모든 API에 `양준형`으로 작성한다.
8. 백엔드 개발 현황은 HEARO_API_SPEC.md의 `개발 현황` 값을 그대로 사용한다.
9. 토큰은 HEARO_API_SPEC.md의 `토큰` 값을 그대로 사용한다.
10. 작업 완료 후 신규 작성, 수정, 변경 없음, 확인 필요, 코드에서 확인되지 않은 기존 API의 개수를 보고한다.

[Notion API 인덱스 열]
다음 열을 기존 Notion 속성에 맞춰 유지하거나, 존재하지 않으면 추가한다.

index | 기능 | HTTP Method | API Path | 담당자 | 개발 현황 | 토큰 | 프론트 담당자 | 프론트 개발 현황

[프론트엔드 관련 필수 제한]
1. 프론트엔드는 현재 기능 검수 중이며 개발이 완료되지 않은 상태다.
2. 프론트엔드 코드는 스캔, 실행, 수정, 포맷팅, 빌드 또는 테스트하지 않는다.
3. 프론트 설정, 패키지, 환경변수와 API 호출 코드를 변경하지 않는다.
4. `프론트 담당자`는 모든 API에 `프론트`로 작성한다.
5. `프론트 개발 현황`에는 개발 완료 여부나 진행률을 쓰지 않는다.
6. `프론트 개발 현황`에는 해당 API가 어떤 화면, 버튼 또는 사용자 동작과 연결되어야 하는지만 설명한다.
7. 기존 Notion에서 화면 번호를 확인할 수 있을 때만 `[1]`, `[2]` 형식의 번호를 사용한다.
8. 확인되지 않은 화면 번호나 프론트 기능을 추측해서 만들지 않는다.
9. 대응 화면을 확인할 수 없으면 `연동 화면 확인 필요`라고 작성한다.
10. 프론트 관련 내용은 설명용 정보이며 프론트 개발 상태를 `완료`로 변경하지 않는다.

[프론트 개발 현황 작성 예시]
- `[1] 로그인 화면 연동 필요`
- `[2] 회원가입 화면 연동 필요`
- `[4] 인증번호 확인 버튼 연동 필요`
- `녹음 완료 후 변환 결과 표시 연동 필요`
- `진료 기록 목록 화면 연동 필요`
- `연동 화면 확인 필요`

[정확성 규칙]
1. HEARO_API_SPEC.md에 없는 API, 필드, 오류 코드, 화면 번호를 추측하지 않는다.
2. API Path의 대소문자와 Path Variable 표기를 그대로 유지한다.
3. `POST /api/mail/check`의 인증번호 불일치는 실제 HTTP 200이며 응답 Body의 status만 `"400"`인 현재 코드 동작을 정확히 적는다.
4. `POST /api/users/change-password`를 PATCH로 바꾸지 않는다.
5. `/api/care/user/Guards`의 대문자 `G`를 임의로 변경하지 않는다.
6. 코드에 존재하지 않는 `/api/users/mypage`와 `/api/hearO`를 신규 API로 작성하지 않는다. 현재 마이페이지 경로는 `/api/mypage/**`다.
7. 비밀번호, JWT 원문, SMTP 정보, API Key, 실제 이메일, 실제 사용자 데이터 및 환경변수 값을 Notion에 기록하지 않는다.
8. HEARO_API_SPEC.md와 프로젝트 소스코드는 수정하지 않는다. 허용된 변경 대상은 Notion API 명세서뿐이다.
9. 기존 Notion 템플릿의 속성명과 상세 페이지 구조를 불필요하게 변경하지 않는다.
10. 일부 API만 처리하고 완료했다고 보고하지 말고 56개 전체를 대조한 뒤 정합성을 검사한다.

[완료 조건]
- Notion 인덱스에 현재 API 56개가 누락 없이 반영되어야 한다.
- `HTTP Method + API Path`가 중복되면 안 된다.
- 각 인덱스 행과 상세 페이지가 연결되어야 한다.
- 토큰 Yes 41개, No 15개가 유지되어야 한다.
- 프론트 담당자와 프론트 개발 현황 열이 위 제한에 맞게 작성되어야 한다.
- 최종 보고에 처리 결과와 확인 필요 항목을 제시해야 한다.
```

### Notion 작업 후 최종 보고 형식

```markdown
## 작업 결과
- 기준 문서: HEARO_API_SPEC.md
- 전체 API: 56개
- 신규 작성: N개
- 기존 항목 수정: N개
- 변경 없음: N개
- 코드에서 확인되지 않은 기존 항목: N개
- 확인 필요: N개

## 인증 분류
- 토큰 Yes: 41개
- 토큰 No: 15개

## 프론트 설명
- 프론트 담당자 입력: N개
- 프론트 연동 설명 입력: N개
- 연동 화면 확인 필요: N개
- 프론트 코드 변경: 없음

## 정합성 검사
- 중복된 HTTP Method + API Path: 없음/있음
- 누락된 API: 없음/있음
- 상세 페이지 연결 누락: 없음/있음

## 확인 필요
- 대상 API
- 확인이 필요한 내용
- 판단을 보류한 이유
```

## 1. API 인덱스

| index | 기능 | HTTP Method | API Path | 담당자 | 개발 현황 | 토큰 |
|---|---|---|---|---|---|---|
| [회원가입] | 사용자 유형별 회원가입 | POST | `/api/users/join` | 양준형 | 완료 | No |
| [로그인] | 로그인 및 JWT 발급 | POST | `/api/users/login` | 양준형 | 완료 | No |
| [아이디 찾기] | 이름과 이메일로 아이디 조회 | POST | `/api/users/find-id` | 양준형 | 완료 | No |
| [비밀번호 변경 인증] | 이메일 인증 상태와 사용자 정보 확인 후 임시 토큰 발급 | POST | `/api/users/to-change-password` | 양준형 | 완료 | No |
| [비밀번호 변경] | 임시 토큰으로 새 비밀번호 설정 | POST | `/api/users/change-password` | 양준형 | 완료 | No |
| [토큰 재발급] | Refresh Token 검증 및 JWT 재발급 | POST | `/api/users/token/reissue` | 양준형 | 완료 | No |
| [메일 인증번호 발송] | 이메일 인증번호 발송 및 Redis 저장 | POST | `/api/mail/send` | 양준형 | 완료 | No |
| [메일 인증번호 확인] | 이메일 인증번호 검증 | POST | `/api/mail/check` | 양준형 | 완료 | No |
| [피보호자 마이페이지] | 피보호자 본인 정보 조회 | GET | `/api/mypage/ward-user` | 양준형 | 완료 | Yes |
| [보호자 마이페이지] | 보호자 본인 정보 조회 | GET | `/api/mypage/guard-user` | 양준형 | 완료 | Yes |
| [기관 마이페이지] | 기관 본인 정보 조회 | GET | `/api/mypage/institutions-user` | 양준형 | 완료 | Yes |
| [이름 변경] | 로그인 사용자 유형에 맞는 테이블의 이름 변경 | PATCH | `/api/mypage/change-name` | 양준형 | 완료 | Yes |
| [피보호자 검색] | 보호자가 연결할 피보호자 검색 | GET | `/api/care/user/search-ward-user` | 양준형 | 완료 | Yes |
| [돌봄 연결 신청] | 보호자가 피보호자에게 연결 신청 | POST | `/api/care/user/save-care` | 양준형 | 완료 | Yes |
| [피보호자 연결 목록] | 피보호자 기준 연결 목록 조회 | GET | `/api/care/user/ward/check-care-list` | 양준형 | 완료 | Yes |
| [보호자 연결 목록] | 보호자 기준 연결 목록 조회 | GET | `/api/care/user/guard/check-care-list` | 양준형 | 완료 | Yes |
| [돌봄 연결 승인] | 피보호자가 연결 요청 승인 | POST | `/api/care/user/change-care-approve` | 양준형 | 완료 | Yes |
| [돌봄 연결 거절] | 피보호자가 연결 요청 거절 | POST | `/api/care/user/change-care-reject` | 양준형 | 완료 | Yes |
| [내 보호자 조회] | 피보호자가 연결된 보호자 목록 조회 | GET | `/api/care/user/wards` | 양준형 | 완료 | Yes |
| [내 피보호자 조회] | 보호자가 연결된 피보호자 목록 조회 | GET | `/api/care/user/Guards` | 양준형 | 완료 | Yes |
| [메인 보호자 변경] | 피보호자가 승인된 보호자 중 메인 보호자를 변경 | POST | `/api/care/check-main-guard` | 양준형 | 완료 | Yes |
| [보호 관계 삭제] | 피보호자 또는 보호자가 본인의 보호 관계 삭제 | POST | `/api/care/delete-care` | 양준형 | 완료 | Yes |
| [공개 기관 검색] | 기관명으로 기관 목록 검색 | GET | `/api/institutions/search` | 양준형 | 완료 | No |
| [기관 가입] | 이메일 인증 후 기관 관리자 계정 생성 | POST | `/api/institutions/join` | 양준형 | 완료 | No |
| [기관 로그인] | 승인된 기관 관리자 로그인 및 JWT 발급 | POST | `/api/institutions/login` | 양준형 | 완료 | No |
| [기관 아이디 찾기] | 기관명과 이메일로 기관 로그인 ID 조회 | POST | `/api/institutions/id-find` | 양준형 | 완료 | No |
| [기관 비밀번호 변경 인증] | 이메일 인증 상태와 기관 정보 확인 후 임시 토큰 발급 | POST | `/api/institutions/to-change-password` | 양준형 | 완료 | No |
| [기관 비밀번호 변경] | 임시 토큰으로 기관 비밀번호 설정 | POST | `/api/institutions/change-password` | 양준형 | 완료 | No |
| [승인 대기 기관 사용자 조회] | 소속 승인 대기 사용자를 페이지 조회 | POST | `/api/institutions/search-pending-user` | 양준형 | 완료 | Yes |
| [승인 기관 사용자 조회] | 소속 승인 사용자를 페이지 조회 | POST | `/api/institutions/search-approved-user` | 양준형 | 완료 | Yes |
| [거절 기관 사용자 조회] | 소속 거절 사용자를 페이지 조회 | POST | `/api/institutions/search-reject-user` | 양준형 | 완료 | Yes |
| [기관 사용자 승인] | 소속 사용자를 승인 상태로 변경 | POST | `/api/institutions/user/approved` | 양준형 | 완료 | Yes |
| [기관 사용자 거절] | 소속 사용자를 거절 상태로 변경 | POST | `/api/institutions/user/reject` | 양준형 | 완료 | Yes |
| [기관 사용자 삭제] | 소속 사용자를 삭제 상태로 변경 | POST | `/api/institutions/user/delete` | 양준형 | 완료 | Yes |
| [의료기관 검색] | 피보호자가 의료기관 사용자 검색 | GET | `/api/medical-treatment/ward/institutions` | 양준형 | 완료 | Yes |
| [진료 요청 생성] | 피보호자가 기관에 진료 요청 | POST | `/api/medical-treatment/ward/requests` | 양준형 | 완료 | Yes |
| [보낸 진료 요청 목록] | 피보호자의 진료 요청 목록 조회 | GET | `/api/medical-treatment/ward/requests` | 양준형 | 완료 | Yes |
| [피보호자 진료 요청 상세] | 피보호자가 진료 요청 상세 조회 | GET | `/api/medical-treatment/ward/requests/{requestId}` | 양준형 | 완료 | Yes |
| [진료 시작] | 생성된 채팅방·아카이브 반환, 기존 수락 요청은 생성 후 반환 | POST | `/api/medical-treatment/ward/requests/{requestId}/start` | 양준형 | 완료 | Yes |
| [받은 진료 요청 목록] | 기관 사용자의 수신 요청 목록 조회 | GET | `/api/medical-treatment/institution/requests` | 양준형 | 완료 | Yes |
| [기관 진료 요청 상세] | 기관 사용자가 진료 요청 상세 조회 | GET | `/api/medical-treatment/institution/requests/{requestId}` | 양준형 | 완료 | Yes |
| [진료 요청 수락] | 기관 사용자가 요청을 수락하고 진료 공간을 즉시 생성 | POST | `/api/medical-treatment/institution/requests/{requestId}/accept` | 양준형 | 완료 | Yes |
| [진료 요청 거절] | 기관 사용자가 진료 요청 거절 | POST | `/api/medical-treatment/institution/requests/{requestId}/reject` | 양준형 | 완료 | Yes |
| [채팅방 조회] | 피보호자가 참여 중인 채팅방 조회 | GET | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}` | 양준형 | 완료 | Yes |
| [피보호자 채팅 조회] | 피보호자가 채팅 메시지 목록 조회 | GET | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages` | 양준형 | 완료 | Yes |
| [텍스트 메시지 전송] | 피보호자가 텍스트 메시지 전송 | POST | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages` | 양준형 | 완료 | Yes |
| [진료 종료] | 진료 종료, AI 요약 생성 및 아카이브 저장 | POST | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/complete` | 양준형 | 완료 | Yes |
| [기관 채팅 조회] | 기관 사용자가 채팅 메시지 목록 조회 | GET | `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/messages` | 양준형 | 완료 | Yes |
| [녹음 완료] | 녹음 파일 음성 변환 및 채팅 메시지 저장 | POST | `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/recordings/complete` | 양준형 | 완료 | Yes |
| [진료 기록 상세] | 피보호자 또는 연결 보호자의 진료 기록 조회 | GET | `/api/medical-treatment/ward/archives/{archiveId}` | 양준형 | 완료 | Yes |
| [내 진료 기록 목록] | 피보호자 본인의 진료 기록 목록 조회 | GET | `/api/medical-treatment/ward/archives/list/for-ward` | 양준형 | 완료 | Yes |
| [피보호자 진료 기록 목록] | 보호자가 연결된 피보호자의 기록 목록 조회 | GET | `/api/medical-treatment/ward/archives/{wardUserId}/list/for-guard` | 양준형 | 완료 | Yes |
| [문의 등록] | 인증 사용자가 문의 등록 | POST | `/api/inquiries` | 양준형 | 완료 | Yes |
| [내 문의 목록] | 인증 사용자가 본인 문의 목록 조회 | GET | `/api/inquiries` | 양준형 | 완료 | Yes |
| [내 문의 상세] | 인증 사용자가 본인 문의 상세 조회 | GET | `/api/inquiries/{inquiryId}` | 양준형 | 완료 | Yes |
| [헬스 체크] | 애플리케이션 상태 확인 | GET | `/api/health` | 양준형 | 완료 | No |

## 2. 공통 규약

### 2.1 인증 헤더

토큰이 `Yes`인 API는 다음 헤더가 필수다.

```http
Authorization: Bearer {accessToken}
```

JWT의 `role` 클레임은 일반 사용자 계정의 `USER`와 기관 관리자 계정의 `INSTITUTION`을 구분한다. `USER` 토큰에는 `userType`이 포함되고, `INSTITUTION` 토큰의 subject에는 기관 ID가 저장된다. 기관 사용자 관리 API는 기관 관리자 토큰만 허용한다.

Refresh Token은 Redis에 역할별 키로 저장하며 재발급 시 원래 토큰과 동일한 역할로 새 토큰을 발급한다.

```text
refresh-token:user:{userId}
refresh-token:institution:{institutionId}
```

| HTTP | Code | Message | 설명 |
|---|---|---|---|
| 401 | AuthenticationException | 인증 토큰이 필요합니다. | 헤더가 없거나 `Bearer ` 형식이 아님 |
| 401 | AuthenticationException | 유효하지 않거나 만료된 인증 토큰입니다. | 서명·만료·토큰 종류 검증 실패 |

### 2.2 공통 응답 래퍼

| 필드 | 타입 | 설명 |
|---|---|---|
| `status` | string | 애플리케이션 상태 코드 문자열 |
| `message` | string | 결과 메시지 |
| `data` | object, array, string, null | 엔드포인트별 응답 데이터 |

```json
{
  "status": "200",
  "message": "요청에 성공했습니다.",
  "data": {}
}
```

서비스의 `IllegalArgumentException`과 `IllegalStateException`은 HTTP 400, `AuthenticationException`은 HTTP 401로 변환된다. 음성 인식·파일 저장·AI 요약 오류는 HTTP 502로 변환된다. 별도 처리되지 않은 예외는 코드만으로 응답 형식을 확정할 수 없다.

### 2.3 공통 데이터 스키마

#### MedicalRequestResponse

| 필드 | 타입 | 설명 |
|---|---|---|
| `medicalRequestId` | number | 진료 요청 ID |
| `wardUserId` | string | 피보호자 ID |
| `wardUserName` | string | 피보호자 이름 |
| `institutionUserId` | string | 기관 사용자 ID |
| `institutionUserName` | string | 기관 사용자 이름 |
| `status` | string | `REQUESTED`, `ACCEPTED`, `REJECTED`, `IN_PROGRESS`, `COMPLETED`, `CANCELED` |
| `createdAt` | string | 생성 일시 |
| `respondedAt` | string/null | 응답 일시 |
| `startedAt` | string/null | 시작 일시 |
| `completedAt` | string/null | 완료 일시 |
| `chatRoomId` | number/null | 생성된 채팅방 ID. 진료 공간 생성 전에는 `null` |
| `archiveId` | number/null | 생성된 아카이브 ID. 진료 공간 생성 전에는 `null` |

#### ChatMessageResponse

| 필드 | 타입 | 설명 |
|---|---|---|
| `messageId` | number | 메시지 ID |
| `chatRoomId` | number | 채팅방 ID |
| `senderType` | string | `INSTITUTION_USER`, `WARD_USER`, `SYSTEM` |
| `senderId` | string | 발신자 ID |
| `senderName` | string | 발신자 이름 |
| `messageType` | string | `TEXT`, `VOICE_TRANSCRIPT`, `SYSTEM` |
| `content` | string | 메시지 또는 음성 변환 내용 |
| `recordId` | number/null | 연결된 녹음 레코드 ID |
| `createdAt` | string | 생성 일시 |
| `mine` | boolean | 현재 요청 사용자 작성 여부 (`isMine` 필드의 Lombok/Jackson 직렬화명) |

#### ChatRoomResponse

| 필드 | 타입 | 설명 |
|---|---|---|
| `chatRoomId` | number | 채팅방 ID |
| `medicalRequestId` | number | 진료 요청 ID |
| `archiveId` | number | 아카이브 ID |
| `institutionUser` | object | `institutionUserId`, `name`, `email` |
| `wardUser` | object | `wardUserId`, `name` |
| `status` | string | `IN_PROGRESS`, `COMPLETED` |
| `startedAt` | string | 시작 일시 |
| `completedAt` | string/null | 완료 일시 |
| `lastMessage` | string/null | 마지막 메시지 |
| `lastMessageAt` | string/null | 마지막 메시지 일시 |

#### AiResponse

| 필드 | 타입 | 설명 |
|---|---|---|
| `wardUserId` | string | 피보호자 ID |
| `archiveId` | number | 아카이브 ID |
| `allChatText` | string | AI 서비스가 반환한 전체 대화 |
| `mainSymptoms` | string | 주요 증상 |
| `doctorOpinion` | string | 의사 소견 |
| `remember` | string | 기억할 내용 |
| `questionAnswer` | string | 질문·답변 요약 |
| `difficultWords` | string | 어려운 용어 설명 |

요약 필드 일부만 비어 있으면 해당 필드는 `"없음"`으로 정규화된다. 응답 자체가 없거나 요약 필드 전체가 비어 있으면 HTTP 502다.

#### ReadArchiveResponse

| 필드 | 타입 | 설명 |
|---|---|---|
| `archiveId` | number | 아카이브 ID |
| `title` | string | 진료 기록 제목 |
| `archiveDate` | string | 아카이브 생성 일시 |
| `text` | string | 진료 기록 텍스트 |
| `allChatText` | string/null | 진료 종료 시 합친 전체 대화 |
| `mainSymptoms` | string/null | 주요 증상 |
| `doctorOpinion` | string/null | 의사 소견 |
| `remember` | string/null | 기억할 내용 |
| `questionAnswer` | string/null | 질문·답변 요약 |
| `difficultWords` | string/null | 어려운 용어 설명 |

#### JudgeUserResponse

| 필드 | 타입 | 설명 |
|---|---|---|
| `totalCount` | number | 해당 상태의 소속 사용자 전체 개수 |
| `currentPage` | number | 현재 페이지 번호(0부터 시작) |
| `pageSize` | number | 요청한 페이지 크기 |
| `hasNext` | boolean | 다음 페이지 존재 여부 |
| `judgeUserList` | array | 소속 사용자 목록 |

`judgeUserList` 원소는 `{ userId, username, userEmail, state, institutionName }` 구조이며 `state`는 `PENDING`, `APPROVED`, `REJECTED`, `DELETE` 중 하나다.

## 3. 엔드포인트 상세

### 1) [회원가입] `/api/users/join`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/users/join` |
| Auth(인증) | No |
| Description | 사용자 유형에 맞는 사용자 테이블에 신규 회원을 저장한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Body | `id` | string | Yes | 5자 이상의 사용자 ID | `ward-demo` |
| Body | `name` | string | Yes | 이름 | `홍길동` |
| Body | `email` | string | Yes | 이메일 | `demo@example.com` |
| Body | `password` | string | Yes | 비밀번호 | `{password}` |
| Body | `checkPassword` | string | Yes | 비밀번호 확인 | `{password}` |
| Body | `userType` | string | Yes | `WARD`, `GUARDIAN`, `INSTITUTIONS` | `WARD` |

#### Response (200)

`data`는 생성된 사용자 ID(string)다. 메시지는 `회원 가입이 완료되었습니다.`이다.

#### Error Codes

| HTTP | Code | Message | 설명 |
|---|---|---|---|
| 400 | IllegalArgumentException | 유효하지 않은 사용자 유형입니다. | 지원하지 않는 사용자 유형 |
| 400 | IllegalStateException | 이미 존재하는 회원입니다. | 세 사용자 테이블 중 동일 ID 존재 |
| 400 | IllegalStateException | 비밀번호가 일치하지 않습니다. | 비밀번호 확인 불일치 |

### 2) [로그인] `/api/users/login`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/users/login` |
| Auth(인증) | No |
| Description | ID와 비밀번호를 확인하고 Access/Refresh Token을 발급한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Body | `id` | string | Yes | 사용자 ID | `ward-demo` |
| Body | `password` | string | Yes | 비밀번호 | `{password}` |

#### Response (200)

| 필드 | 타입 | 설명 |
|---|---|---|
| `data.accessToken` | string | JWT Access Token |
| `data.userId` | string | 사용자 ID |
| `data.userType` | string | 사용자 유형 |
| `data.refreshToken` | string | JWT Refresh Token |

#### Error Codes

| HTTP | Code | Message | 설명 |
|---|---|---|---|
| 400 | IllegalArgumentException | 유효하지 않은 사용자 ID입니다. | ID가 어느 사용자 테이블에도 없음 |
| 400 | IllegalArgumentException | 아이디가 올바르지 않습니다. | 선택된 유형의 사용자를 찾지 못함 |
| 400 | IllegalArgumentException | 비밀번호가 올바르지 않습니다. | 비밀번호 불일치 |

### 3) [아이디 찾기] `/api/users/find-id`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/users/find-id` |
| Auth(인증) | No |
| Description | 이름과 이메일이 일치하는 사용자 ID를 반환한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Body | `name` | string | Yes | 사용자 이름 | `홍길동` |
| Body | `email` | string | Yes | 사용자 이메일 | `demo@example.com` |

#### Response (200)

`data`는 조회된 사용자 ID(string), 메시지는 `ID 찾기가 성공했습니다.`이다.

#### Error Codes

| HTTP | Code | Message | 설명 |
|---|---|---|---|
| 400 | IllegalArgumentException | 유효하지 않은 사용자 ID입니다. | 이메일에 해당하는 사용자 유형 없음 |
| 400 | IllegalArgumentException | 아이디를 찾을 수 없습니다. | 이름과 이메일이 일치하지 않음 |

### 4) [비밀번호 변경 인증] `/api/users/to-change-password`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/users/to-change-password` |
| Auth(인증) | No |
| Description | 10분 이내 이메일 인증 여부와 사용자 정보를 확인하고 3분 유효 임시 토큰을 발급한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Body | `name` | string | Yes | 사용자 이름 | `홍길동` |
| Body | `email` | string | Yes | 인증 완료 이메일 | `demo@example.com` |

#### Response (200)

| 필드 | 타입 | 설명 |
|---|---|---|
| `data.id` | string | 사용자 ID |
| `data.userType` | string | 사용자 유형 |
| `data.tempToken` | string | 비밀번호 변경용 임시 토큰 |

#### Error Codes

| HTTP | Code | Message | 설명 |
|---|---|---|---|
| 400 | IllegalArgumentException | 이메일 인증이 완료되지 않았습니다. 이메일 인증을 먼저 진행해주세요. | Redis 인증 상태 없음 |
| 400 | IllegalArgumentException | 이메일이 올바르지 않습니다. {email} | 이메일 사용자 없음 |
| 400 | IllegalArgumentException | 이름이 올바르지 않습니다. {name} | 이름 불일치 |

### 5) [비밀번호 변경] `/api/users/change-password`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/users/change-password` |
| Auth(인증) | No |
| Description | 임시 토큰을 검증하고 사용자 비밀번호를 변경한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Body | `id` | string | Yes | 사용자 ID | `ward-demo` |
| Body | `newPassword` | string | Yes | 새 비밀번호 | `{newPassword}` |
| Body | `checkNewPassword` | string | Yes | 새 비밀번호 확인 | `{newPassword}` |
| Body | `userType` | string | Yes | 사용자 유형 | `WARD` |
| Body | `tempToken` | string | Yes | 임시 인증 토큰 | `{tempToken}` |

#### Response (200)

`data`는 비밀번호를 변경한 사용자 ID다.

#### Error Codes

| HTTP | Code | Message | 설명 |
|---|---|---|---|
| 400 | IllegalArgumentException | 인증 시간이 만료되었거나 유효하지 않은 접근입니다. 처음부터 다시 시도해주세요. | 임시 토큰 없음·만료·불일치 |
| 400 | IllegalArgumentException | 아이디가 올바르지 않습니다. {id} | 사용자 없음 |
| 400 | IllegalArgumentException | 비밀번호가 일치하지 않습니다. | 새 비밀번호 확인 불일치 |

### 6) [토큰 재발급] `/api/users/token/reissue`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/users/token/reissue` |
| Auth(인증) | No |
| Description | 요청 Body의 Refresh Token과 역할별 Redis 저장값을 검증해 동일 계정 역할의 토큰 쌍을 재발급한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Body | `refreshToken` | string | Yes | JWT Refresh Token | `{refreshToken}` |

#### Response (200)

`data.accessToken`, `data.refreshToken`을 반환한다.

일반 사용자 토큰은 `refresh-token:user:{userId}`, 기관 관리자 토큰은 `refresh-token:institution:{institutionId}` 키로 검증하고 교체한다.

#### Error Codes

| HTTP | Code | Message | 설명 |
|---|---|---|---|
| 400 | IllegalArgumentException | Refresh Token이 아닙니다. | 토큰 종류 불일치 |
| 400 | IllegalArgumentException | 저장된 Refresh Token이 없습니다. | Redis 저장값 없음 |
| 400 | IllegalArgumentException | Refresh Token이 일치하지 않습니다. | 저장값과 요청값 불일치 |
| 확인 필요 | JWT RuntimeException | 프레임워크 메시지 | 잘못된 서명·만료 JWT는 전용 핸들러가 없음 |

### 7) [메일 인증번호 발송] `/api/mail/send`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/mail/send` |
| Auth(인증) | No |
| Description | 6자리 인증번호를 메일로 보내고 Redis에 3분간 저장한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Body | `email` | string | Yes | 수신 이메일 | `demo@example.com` |

#### Response (200)

`data`는 null, 메시지는 `인증번호가 발송되었습니다.`이다. 메일·Redis 장애는 전용 예외 처리가 없어 실제 오류 응답은 확인 필요다.

### 8) [메일 인증번호 확인] `/api/mail/check`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/mail/check` |
| Auth(인증) | No |
| Description | Redis 인증번호를 비교하고 성공 시 인증 상태를 10분간 저장한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Body | `email` | string | Yes | 이메일 | `demo@example.com` |
| Body | `checkNumber` | string | Yes | 6자리 인증번호 | `123456` |

#### Response (200)

- 일치: Body `status: "200"`, 메시지 `인증번호가 일치합니다.`
- 불일치·만료: **실제 HTTP는 200**이고 Body `status: "400"`, 메시지 `인증번호가 일치하지 않습니다.`

### 9) [피보호자 검색] `/api/care/user/search-ward-user`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/care/user/search-ward-user` |
| Auth(인증) | Yes (GUARDIAN) |
| Description | 보호자가 ID 검색어로 피보호자를 페이지 조회한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Query | `wardUserId` | string | No | 피보호자 ID 검색어 | `ward` |
| Query | `page` | number | No | 기본 0 | `0` |
| Query | `size` | number | No | 기본 10 | `10` |
| Query | `sort` | string | No | 기본 `id,asc` | `id,asc` |

#### Response (200)

`data`: `totalCount`, `currentPage`, `pageSize`, `hasNext`, `wardUserList[] { wardUserId, wardUserName }`.

#### Error Codes

HTTP 400 `보호자만 검색할 수 있는 기능입니다.` 및 공통 인증 오류.

### 10) [돌봄 연결 신청] `/api/care/user/save-care`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/care/user/save-care` |
| Auth(인증) | Yes (GUARDIAN) |
| Description | 보호자와 피보호자 사이에 PENDING 연결을 생성한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Body | `wardUserId` | string | Yes | 연결할 피보호자 ID | `ward-demo` |

#### Response (200)

`data.careId`(number)를 반환한다.

#### Error Codes

HTTP 400: `보호자만 연결을 신청할 수 있습니다.`, `보호자를 찾을 수 없습니다.`, `피보호자를 찾을 수 없습니다.`, `이미 신청 되었거나 연결된 사용자입니다.` 및 공통 인증 오류.

### 11) [피보호자 연결 목록] `/api/care/user/ward/check-care-list`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/care/user/ward/check-care-list` |
| Auth(인증) | Yes (WARD) |
| Description | 피보호자의 연결 요청·상태 목록을 조회한다. |

#### Request

요청 데이터 없음.

#### Response (200)

`data.totalCount`, `data.careList[] { wardUserId, guardUserId, careState, createdAt, updatedAt, careId }`. `careState`: `APPROVED`, `PENDING`, `REJECTED`.

#### Error Codes

HTTP 400: `피보호자만 연결 목록을 조회할 수 있습니다`, `피보호자를 찾을 수 없습니다.` 및 공통 인증 오류.

### 12) [보호자 연결 목록] `/api/care/user/guard/check-care-list`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/care/user/guard/check-care-list` |
| Auth(인증) | Yes (GUARDIAN) |
| Description | 보호자의 연결 요청·상태 목록을 조회한다. |

#### Request

요청 데이터 없음.

#### Response (200)

11번과 동일한 `CheckCareListResponse` 구조다.

#### Error Codes

HTTP 400: `보호자만 연결 목록을 조회할 수 있습니다`, `보호자를 찾을 수 없습니다.` 및 공통 인증 오류.

### 13) [돌봄 연결 승인] `/api/care/user/change-care-approve`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/care/user/change-care-approve` |
| Auth(인증) | Yes (WARD) |
| Description | 본인에게 온 연결 요청을 승인한다. 승인된 메인 보호자가 없으면 최초 승인 보호자를 메인으로 자동 지정한다. |

#### Request

Body `careId`(number, 필수, 예: `1`).

#### Response (200)

`data { careId, careState }`; 성공 상태는 `APPROVED`.

#### Error Codes

HTTP 400: `피보호자만 연결 요청을 변경할 수 있습니다.`, `연결 요청을 찾을 수 없거나 변경 권한이 없습니다.` 및 공통 인증 오류.

### 14) [돌봄 연결 거절] `/api/care/user/change-care-reject`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/care/user/change-care-reject` |
| Auth(인증) | Yes (WARD) |
| Description | 본인에게 온 연결 요청을 거절한다. |

#### Request

Body `careId`(number, 필수, 예: `1`).

#### Response (200)

`data { careId, careState }`; 성공 상태는 `REJECTED`.

#### Error Codes

13번과 동일하다.

### 15) [내 보호자 조회] `/api/care/user/wards`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/care/user/wards` |
| Auth(인증) | Yes (WARD) |
| Description | 피보호자가 연결된 보호자 목록을 조회한다. |

#### Request

요청 데이터 없음.

#### Response (200)

`data.totalCount`, `data.guardSearchList[] { careId, guardUserId, guardUserName, userType, mainGuardUser }`.

#### Error Codes

HTTP 400: `피보호자만 보호자를 조회할 수 있습니다.`, `피보호자를 찾을 수 없습니다.` 및 공통 인증 오류.

### 16) [내 피보호자 조회] `/api/care/user/Guards`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/care/user/Guards` (대문자 `G` 주의) |
| Auth(인증) | Yes (GUARDIAN) |
| Description | 보호자가 연결된 피보호자 목록을 조회한다. |

#### Request

요청 데이터 없음.

#### Response (200)

`data.totalCount`, `data.wardSearchList[] { careId, wardUserId, wardUserName, userType, mainGuardUser }`.

#### Error Codes

HTTP 400: `보호자만 피보호자를 조회할 수 있습니다.`, `보호자를 찾을 수 없습니다.` 및 공통 인증 오류.

### 17) [메인 보호자 변경] `/api/care/check-main-guard`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/care/check-main-guard` |
| Auth(인증) | Yes (WARD) |
| Description | 현재 메인 보호자를 해제하고, 본인과 연결된 승인 상태의 보호자를 새 메인 보호자로 설정한다. |

#### Request

Body `changeGuardUserId`(string, 필수). 변경을 요청하는 피보호자는 인증 토큰의 사용자 ID를 기준으로 처리한다.

```json
{
  "changeGuardUserId": "guard-demo"
}
```

#### Response (200)

`data.deleteMainCare`는 변경 전 메인 보호 관계 ID, `data.changeMainCare`는 변경 후 메인 보호 관계 ID다.

```json
{
  "status": "200",
  "message": "메인보호자 변경이 완료되었습니다.",
  "data": {
    "deleteMainCare": 1,
    "changeMainCare": 2
  }
}
```

#### Error Codes

HTTP 400: `피보호자만 본인의 메인 보호자를 설정할 수 있습니다.`, `피보호자를 찾을 수 없습니다.`, `보호자를 찾을 수 없습니다.`, `메인 보호자 또는 피보호자를 찾을 수 없습니다.`, `변경될 메인 보호자 또는 피보호자를 찾을 수 없습니다.` 및 공통 인증 오류.

### 18) [보호 관계 삭제] `/api/care/delete-care`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/care/delete-care` |
| Auth(인증) | Yes (WARD, GUARDIAN) |
| Description | 목록에서 전달받은 Care ID를 기준으로 로그인 사용자가 당사자인 보호 관계를 삭제한다. |

#### Request

Body `deleteCareId`(number, 필수).

```json
{
  "deleteCareId": 1
}
```

#### Response (200)

`data`는 `null`, 메시지는 `선택하신 보호자가 삭제되었습니다.`이다.

#### Error Codes

HTTP 400: `피보호자, 보호자만 케어를 삭제할 수 있습니다.`, `케어를 찾을 수 없습니다.`, `삭제 권한이 없는 보호 관계입니다.` 및 공통 인증 오류.

### 19) [의료기관 검색] `/api/medical-treatment/ward/institutions`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/ward/institutions` |
| Auth(인증) | Yes (WARD) |
| Description | 키워드로 기관 사용자를 검색한다. |

#### Request

Query `keyword`(string, 선택).

#### Response (200)

`data[] { institutionUserId, name, email }`.

#### Error Codes

HTTP 400 `해당 사용자 유형은 이 기능을 사용할 수 없습니다.` 및 공통 인증 오류.

### 20) [진료 요청 생성] `/api/medical-treatment/ward/requests`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/medical-treatment/ward/requests` |
| Auth(인증) | Yes (WARD) |
| Description | 선택한 기관 사용자에게 진료 요청을 생성한다. |

#### Request

Body `institutionUserId`(string, 필수, 예: `institution-demo`).

#### Response (200)

`data`는 `MedicalRequestResponse`.

#### Error Codes

HTTP 400: 잘못된 사용자 유형, `피보호자를 찾을 수 없습니다.`, `기관 사용자를 찾을 수 없습니다.`, `이미 처리 중인 진료 요청이 있습니다.` 및 공통 인증 오류.

### 21) [보낸 진료 요청 목록] `/api/medical-treatment/ward/requests`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/ward/requests` |
| Auth(인증) | Yes (WARD) |
| Description | 본인이 보낸 진료 요청을 최신순으로 조회한다. |

#### Request

요청 데이터 없음.

#### Response (200)

`data[]`는 `MedicalRequestResponse` 배열. 진료 공간이 생성된 요청에는 `chatRoomId`, `archiveId`가 포함되며, 생성 전에는 두 필드가 `null`이다. 잘못된 사용자 유형은 HTTP 400.

### 22) [피보호자 진료 요청 상세] `/api/medical-treatment/ward/requests/{requestId}`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/ward/requests/{requestId}` |
| Auth(인증) | Yes |
| Description | 참여자인 피보호자가 진료 요청 상세를 조회한다. |

#### Request

Path `requestId`(number, 필수).

#### Response (200)

`data`는 `MedicalRequestResponse`. 진료 공간이 생성된 요청에는 `chatRoomId`, `archiveId`가 포함되며, 생성 전에는 두 필드가 `null`이다.

#### Error Codes

HTTP 400: `진료 요청을 찾을 수 없습니다.`, `해당 진료 요청을 조회할 권한이 없습니다.` 및 공통 인증 오류.

### 23) [진료 시작] `/api/medical-treatment/ward/requests/{requestId}/start`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/medical-treatment/ward/requests/{requestId}/start` |
| Auth(인증) | Yes (WARD) |
| Description | 이미 생성된 Archive·ChatRoom ID를 반환한다. 공간이 없는 기존 `ACCEPTED` 요청은 Archive·ChatRoom·첫 메시지를 생성하고 진료 중으로 전환한다. |

#### Request

Path `requestId`(number, 필수).

#### Response (200)

`data { chatRoomId, archiveId }`. 동일 요청에 이미 진료 공간이 있으면 기존 ID를 반환하며 중복 생성하지 않는다.

#### Error Codes

HTTP 400: 요청 없음, 잘못된 사용자 유형, `요청을 보낸 피보호자만 진료를 시작할 수 있습니다.`, `현재 상태에서는 요청을 처리할 수 없습니다. 현재 상태: {status}` 및 공통 인증 오류.

### 24) [받은 진료 요청 목록] `/api/medical-treatment/institution/requests`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/institution/requests` |
| Auth(인증) | Yes (INSTITUTIONS) |
| Description | 기관 사용자가 받은 진료 요청을 최신순 조회한다. |

#### Request

요청 데이터 없음.

#### Response (200)

`data[]`는 `MedicalRequestResponse` 배열. 진료 공간이 생성된 요청에는 `chatRoomId`, `archiveId`가 포함되며, 생성 전에는 두 필드가 `null`이다. 잘못된 사용자 유형은 HTTP 400.

### 25) [기관 진료 요청 상세] `/api/medical-treatment/institution/requests/{requestId}`

22번과 같은 요청·응답 구조이며 Path만 기관 경로다. 요청 대상 기관 사용자 또는 요청 피보호자만 조회할 수 있다.

### 26) [진료 요청 수락] `/api/medical-treatment/institution/requests/{requestId}/accept`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/medical-treatment/institution/requests/{requestId}/accept` |
| Auth(인증) | Yes (INSTITUTIONS) |
| Description | 요청 대상 기관 사용자가 요청을 수락하고 동일 트랜잭션에서 Archive·ChatRoom·첫 메시지를 생성한 뒤 `IN_PROGRESS`로 전환한다. |

#### Request / Response

Path `requestId`(number, 필수), 응답 `data`는 `MedicalRequestResponse`. 성공 시 `status`는 `IN_PROGRESS`이며 `chatRoomId`, `archiveId`가 포함된다.

#### Error Codes

HTTP 400: 요청 없음, 잘못된 사용자 유형, `요청 대상 기관 사용자만 처리할 수 있습니다.`, 현재 상태 처리 불가 및 공통 인증 오류. 공간 생성 도중 오류가 발생하면 요청 상태 변경과 생성 작업은 함께 롤백된다.

### 27) [진료 요청 거절] `/api/medical-treatment/institution/requests/{requestId}/reject`

26번과 같은 구조이며 요청 상태를 `REJECTED`로 변경한다.

### 28) [채팅방 조회] `/api/medical-treatment/ward/chat-rooms/{chatRoomId}`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}` |
| Auth(인증) | Yes |
| Description | 참여 중인 채팅방의 상세 정보를 조회한다. |

#### Request / Response

Path `chatRoomId`(number, 필수), `data`는 `ChatRoomResponse`.

#### Error Codes

HTTP 400: `채팅방을 찾을 수 없습니다.`, `채팅방 참여자가 아닙니다.` 및 공통 인증 오류.

### 29) [피보호자 채팅 조회] `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages` |
| Auth(인증) | Yes |
| Description | 참여 채팅방의 메시지를 생성 일시·ID 오름차순으로 조회한다. |

#### Request / Response

Path `chatRoomId`(number, 필수), `data[]`는 `ChatMessageResponse` 배열.

#### Error Codes

26번과 동일하다.

### 30) [텍스트 메시지 전송] `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/messages` |
| Auth(인증) | Yes (WARD) |
| Description | 채팅방 피보호자가 텍스트 메시지를 저장한다. |

#### Request

Path `chatRoomId`(number, 필수), Body `content`(string, 필수·빈 문자열 불가).

#### Response (200)

`data`는 `ChatMessageResponse`.

#### Error Codes

HTTP 400: 잘못된 사용자 유형, 채팅방 없음, `해당 채팅방의 피보호자가 아닙니다.`, `이미 종료된 진료입니다.`, `채팅 메시지는 비어 있을 수 없습니다.` 및 공통 인증 오류.

### 31) [진료 종료] `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/complete`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/medical-treatment/ward/chat-rooms/{chatRoomId}/complete` |
| Auth(인증) | Yes (WARD) |
| Description | 시스템 메시지를 제외한 대화를 합쳐 AI 요약을 생성하고 Archive에 저장한 뒤 진료·채팅방을 완료 처리한다. |

#### Request

Path `chatRoomId`(number, 필수).

#### Response (200)

`data`는 `AiResponse`다. 서버가 시간순으로 합친 전체 대화는 Archive의 `allChatText`에 저장되고, `mainSymptoms`, `doctorOpinion`, `remember`, `questionAnswer`, `difficultWords`도 같은 Archive에 저장된다.

#### Error Codes

HTTP 400: 잘못된 사용자 유형, 채팅방 없음, `해당 진료의 피보호자만 진료를 종료할 수 있습니다.`, `이미 종료된 진료입니다.` 및 공통 인증 오류. AI 서비스 연결 실패, 응답 없음, 전체 요약 필드가 빈 값인 경우 HTTP 502.

### 32) [기관 채팅 조회] `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/messages`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/messages` |
| Auth(인증) | Yes |
| Description | 기관 사용자가 참여 채팅방 메시지를 시간순 조회한다. |

#### Request / Response

Path `chatRoomId`(number, 필수), `data[]`는 `ChatMessageResponse` 배열. 채팅방 없음·비참여 시 HTTP 400.

### 33) [녹음 완료] `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/recordings/complete`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/medical-treatment/institution/chat-rooms/{chatRoomId}/recordings/complete` |
| Auth(인증) | Yes (INSTITUTIONS) |
| Description | 녹음 파일을 음성 인식하고 Record와 VOICE_TRANSCRIPT 메시지를 저장한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|---|
| Path | `chatRoomId` | number | Yes | 채팅방 ID | `1` |
| Multipart | `file` | file | Yes | 녹음 파일. 컨트롤러에는 확장자·크기 제한 없음 | `recording.m4a` |

Content-Type: `multipart/form-data`.

#### Response (200)

`data`는 `ChatMessageResponse`, `messageType`은 `VOICE_TRANSCRIPT`, `recordId`가 연결된다.

#### Error Codes

HTTP 400: 잘못된 사용자 유형, 채팅방 없음, 기관 소유권 없음, 종료된 진료, `녹음 파일이 필요합니다.`, `음성 변환 결과가 비어 있습니다.`, 지원하지 않는 확장자·MIME 타입, 20MB 초과. 음성 인식·파일 저장 실패는 HTTP 502.

### 34) [진료 기록 상세] `/api/medical-treatment/ward/archives/{archiveId}`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/ward/archives/{archiveId}` |
| Auth(인증) | Yes |
| Description | 본인 피보호자 또는 연결된 보호자가 진료 기록을 조회한다. |

#### Request

Path `archiveId`(number, 필수).

#### Response (200)

`data { archiveId, title, archiveDate, text, allChatText, mainSymptoms, doctorOpinion, remember, questionAnswer, difficultWords }`. 진료 종료 전이거나 기존 데이터에는 전체 대화와 요약 필드가 `null`일 수 있다.

#### Error Codes

HTTP 400: `진료 기록을 찾을 수 없습니다.`, `해당 아카이브에 접근할 수 있는 권한이 없습니다.` 및 공통 인증 오류.

### 35) [내 진료 기록 목록] `/api/medical-treatment/ward/archives/list/for-ward`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/ward/archives/list/for-ward` |
| Auth(인증) | Yes (WARD) |
| Description | 피보호자가 자신의 진료 기록을 페이지 조회한다. |

#### Request

Query `page`(기본 0), `size`(기본 10), `sort`(기본 `id,asc`).

#### Response (200)

`data { totalCount, currentPage, pageSize, hasNext, list[] { archiveId, archiveName, archiveDate } }`.

#### Error Codes

HTTP 400 `피보호자만 본인의 진료 기록 목록을 조회할 수 있습니다.` 및 공통 인증 오류.

### 36) [피보호자 진료 기록 목록] `/api/medical-treatment/ward/archives/{wardUserId}/list/for-guard`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/medical-treatment/ward/archives/{wardUserId}/list/for-guard` |
| Auth(인증) | Yes (GUARDIAN) |
| Description | 보호자가 연결 관계에 있는 피보호자의 기록을 페이지 조회한다. |

#### Request

Path `wardUserId`(string, 필수), Query `page`(0), `size`(10), `sort`(`id,asc`).

#### Response (200)

35번과 같은 `FindAllArchiveResponse` 구조다.

#### Error Codes

HTTP 400: `보호자만 진료 기록 목록을 조회할 수 있습니다.`, `해당 아카이브에 접근할 수 있는 권한이 없습니다.` 및 공통 인증 오류.

### 37) [헬스 체크] `/api/health`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/health` |
| Auth(인증) | No |
| Description | 애플리케이션 HTTP 응답 가능 상태를 확인한다. |

#### Request

요청 데이터 없음.

#### Response (200)

공통 `Result`를 사용하지 않는다.

```json
{
  "status": "UP"
}
```

### 38) [피보호자 마이페이지] `/api/mypage/ward-user`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/mypage/ward-user` |
| Auth(인증) | Yes (WARD) |
| Description | JWT의 사용자 ID로 피보호자 본인의 마이페이지 정보를 조회한다. |

#### Response (200)

`data { userId, username, email, userType }`.

#### Error Codes

HTTP 400: `피보호자만 볼 수 있는 기능입니다.`, `피보호자를 찾을 수 없습니다.` 및 공통 인증 오류.

### 39) [보호자 마이페이지] `/api/mypage/guard-user`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/mypage/guard-user` |
| Auth(인증) | Yes (GUARDIAN) |
| Description | JWT의 사용자 ID로 보호자 본인의 마이페이지 정보를 조회한다. |

#### Response (200)

`data { userId, username, email, userType }`.

#### Error Codes

HTTP 400: `보호자만 볼 수 있는 기능입니다.`, `보호자를 찾을 수 없습니다.` 및 공통 인증 오류.

### 40) [기관 마이페이지] `/api/mypage/institutions-user`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/mypage/institutions-user` |
| Auth(인증) | Yes (INSTITUTIONS) |
| Description | JWT의 사용자 ID로 기관 본인의 마이페이지 정보를 조회한다. |

#### Response (200)

`data { userId, username, email, userType, institytionsId }`. `institytionsId`는 현재 코드의 JSON 필드명을 그대로 표기했다.

#### Error Codes

HTTP 400: `기관만 볼 수 있는 기능입니다.`, `기관 사용자를 찾을 수 없습니다.` 및 공통 인증 오류.

### 41) [이름 변경] `/api/mypage/change-name`

| 항목 | 값 |
|---|---|
| Method | PATCH |
| Path | `/api/mypage/change-name` |
| Auth(인증) | Yes (WARD, GUARDIAN, INSTITUTIONS) |
| Description | JWT 사용자 유형에 맞는 사용자 테이블의 이름을 변경한다. |

#### Request

```json
{
  "newName": "변경할 이름"
}
```

#### Response (200)

`data { newName }`, 메시지는 `이름이 변경되었습니다.`이다.

#### Error Codes

HTTP 400: `이름을 변경할 대상을 찾을 수 없습니다.` 및 사용자 유형별 조회 오류, `@NotBlank` 검증 오류, 공통 인증 오류.

### 42) [공개 기관 검색] `/api/institutions/search`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/institutions/search` |
| Auth(인증) | No |
| Description | 기관명에 검색어가 포함된 기관을 대소문자 구분 없이 페이지 조회한다. |

#### Request

Query `keyword`(기본 빈 문자열), `page`(기본 0), `size`(기본 10), `sort`(기본 `id,asc`).

#### Response (200)

공통 `Result`를 사용하지 않고 Spring Data `Page` JSON을 직접 반환한다. `content[] { institutionId, institutionName }`.

### 43) [문의 등록] `/api/inquiries`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/inquiries` |
| Auth(인증) | Yes (WARD, GUARDIAN, INSTITUTIONS) |
| Description | 로그인 사용자의 문의를 등록한다. |

#### Request

Body `title`(string, 필수, 공백 제외 100자 이하), `content`(string, 필수, 공백 제외 5,000자 이하).

#### Response (201)

HTTP 201, `data { inquiryId, userId, userName, userType, title, content, status, answer, createdAt, answeredAt }`. 최초 `status`는 `PENDING`, `answer`와 `answeredAt`은 `null`이다.

#### Error Codes

HTTP 400: `문의 제목을 입력해 주세요.`, `문의 제목은 100자 이하여야 합니다.`, `문의 내용을 입력해 주세요.`, `문의 내용은 5000자 이하여야 합니다.` 및 공통 인증 오류.

### 44) [내 문의 목록] `/api/inquiries`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/inquiries` |
| Auth(인증) | Yes (WARD, GUARDIAN, INSTITUTIONS) |
| Description | 로그인 사용자가 자신의 문의만 생성일시 내림차순으로 조회한다. |

#### Request

Query `page`(기본 0), `size`(기본 10).

#### Response (200)

`data { totalElements, page, size, hasNext, inquiries[] }`. 각 문의는 43번의 `InquiryResponse` 구조다.

### 45) [내 문의 상세] `/api/inquiries/{inquiryId}`

| 항목 | 값 |
|---|---|
| Method | GET |
| Path | `/api/inquiries/{inquiryId}` |
| Auth(인증) | Yes (WARD, GUARDIAN, INSTITUTIONS) |
| Description | 로그인 사용자가 자신의 문의 상세를 조회한다. |

#### Request / Response

Path `inquiryId`(number, 필수), `data`는 43번의 `InquiryResponse` 구조다.

#### Error Codes

HTTP 400: `문의를 찾을 수 없거나 접근 권한이 없습니다.` 및 공통 인증 오류.

### 46) [기관 가입] `/api/institutions/join`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/join` |
| Auth(인증) | No |
| Description | 이메일 인증과 중복 여부를 확인한 뒤 `PENDING` 상태의 기관 관리자 계정을 생성한다. |

#### Request

| 구분 | 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|---|
| Body | `institutionName` | string | Yes | 기관명 |
| Body | `email` | string | Yes | 인증을 완료한 기관 이메일 |
| Body | `institutionId` | string | Yes | 기관 로그인 ID |
| Body | `password` | string | Yes | 비밀번호 |
| Body | `checkPassword` | string | Yes | 비밀번호 확인 |

#### Response (200)

`data { id }`를 반환한다. `id`는 생성된 기관의 number ID이며 메시지는 `정상적으로 기관 회원가입이 완료되었습니다.`다.

#### Error Codes

HTTP 400: 이메일 인증 미완료, `이미 가입된 이메일입니다.`, `이미 존재하는 아이디입니다.`, `비밀번호가 일치하지 않습니다.`.

### 47) [기관 로그인] `/api/institutions/login`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/login` |
| Auth(인증) | No |
| Description | 승인된 기관 관리자 계정의 ID와 비밀번호를 확인하고 기관 역할 JWT를 발급한다. |

#### Request

Body `loginId`(string, 필수), `password`(string, 필수).

#### Response (200)

`data { accessToken, institutionId, refreshToken }`. JWT의 `role`은 `INSTITUTION`이고 subject는 기관 ID다. Refresh Token은 `refresh-token:institution:{institutionId}` 키로 Redis에 저장된다.

#### Error Codes

HTTP 400: `아이디가 올바르지 않습니다.`, `비밀번호가 올바르지 않습니다.`, `승인된 기관만 로그인할 수 있습니다.`.

### 48) [기관 아이디 찾기] `/api/institutions/id-find`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/id-find` |
| Auth(인증) | No |
| Description | 기관명과 이메일이 일치하는 기관 로그인 ID를 반환한다. |

#### Request / Response

Body `institutionName`(string, 필수), `email`(string, 필수, 이메일 형식). `data`는 기관 로그인 ID(string)이며 메시지는 `아이디를 찾았습니다.`다.

#### Error Codes

HTTP 400: `아이디를 찾을 수 없습니다.` 및 `@NotBlank`, `@Email` 검증 오류.

### 49) [기관 비밀번호 변경 인증] `/api/institutions/to-change-password`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/to-change-password` |
| Auth(인증) | No |
| Description | 이메일 인증 상태와 기관 정보를 확인하고 3분 유효 비밀번호 재설정 토큰을 발급한다. |

#### Request

Body `institutionName`(string, 필수), `email`(string, 필수, 이메일 형식).

#### Response (200)

`data { institutionId, tempToken }`.

#### Error Codes

HTTP 400: `이메일 인증이 완료되지 않았습니다. 이메일 인증을 먼저 진행해주세요.`, `이메일이 올바르지 않습니다. {email}`, `기관명이 올바르지 않습니다. {institutionName}` 및 요청값 검증 오류.

### 50) [기관 비밀번호 변경] `/api/institutions/change-password`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/change-password` |
| Auth(인증) | No |
| Description | 임시 토큰을 검증하고 기관 관리자 계정의 비밀번호를 변경한다. |

#### Request

Body `institutionId`(number, 필수), `newPassword`(string, 필수), `checkNewPassword`(string, 필수), `tempToken`(string, 필수).

#### Response (200)

`data`는 비밀번호를 변경한 기관 ID(number)다.

#### Error Codes

HTTP 400: 임시 토큰 없음·만료·불일치, `비밀번호가 일치하지 않습니다.`, `기관을 찾을 수 없습니다.` 및 요청값 검증 오류.

### 51) [승인 대기 기관 사용자 조회] `/api/institutions/search-pending-user`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/search-pending-user` |
| Auth(인증) | Yes (`INSTITUTION` 역할) |
| Description | 현재 기관에 소속된 `PENDING` 상태 사용자를 페이지 조회한다. |

#### Request

기관 ID와 상태는 Access Token과 엔드포인트에서 결정된다. 현재 Controller 선언상 JSON Body가 필요하지만 `JudgeUserRequest`의 필드는 조회에 사용하지 않으므로 빈 객체 `{}`를 전달한다. Query `page`(기본 0), `size`(기본 10).

#### Response (200)

`data`는 `JudgeUserResponse` 구조다.

#### Error Codes

HTTP 400: `기관을 찾을 수 없습니다.`, `승인된 기관만 접근할 수 있습니다.`. HTTP 401: 기관 토큰 누락·만료·형식 오류 또는 일반 사용자 토큰 사용.

### 52) [승인 기관 사용자 조회] `/api/institutions/search-approved-user`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/search-approved-user` |
| Auth(인증) | Yes (`INSTITUTION` 역할) |
| Description | 현재 기관에 소속된 `APPROVED` 상태 사용자를 페이지 조회한다. |

요청·응답·오류 구조는 51번과 같으며 목록과 `totalCount` 조건만 `APPROVED`다.

### 53) [거절 기관 사용자 조회] `/api/institutions/search-reject-user`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/search-reject-user` |
| Auth(인증) | Yes (`INSTITUTION` 역할) |
| Description | 현재 기관에 소속된 `REJECTED` 상태 사용자를 페이지 조회한다. |

요청·응답·오류 구조는 51번과 같으며 목록과 `totalCount` 조건만 `REJECTED`다.

### 54) [기관 사용자 승인] `/api/institutions/user/approved`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/user/approved` |
| Auth(인증) | Yes (`INSTITUTION` 역할) |
| Description | 현재 기관 소속 사용자의 상태를 `APPROVED`로 변경한다. |

#### Request / Response

Body `institutionsUserId`(string, 필수). `data { institutionsUserId, institutionState }`이며 `institutionState`는 `APPROVED`다.

#### Error Codes

HTTP 400: `기관 사용자를 찾을 수 없습니다.`, `해당 기관의 사용자가 아닙니다.`, 기관 없음·미승인. HTTP 401: 기관 토큰 검증 실패.

### 55) [기관 사용자 거절] `/api/institutions/user/reject`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/user/reject` |
| Auth(인증) | Yes (`INSTITUTION` 역할) |
| Description | 현재 기관 소속 사용자의 상태를 `REJECTED`로 변경한다. |

요청·응답·오류 구조는 54번과 같으며 응답 `institutionState`는 `REJECTED`다.

### 56) [기관 사용자 삭제] `/api/institutions/user/delete`

| 항목 | 값 |
|---|---|
| Method | POST |
| Path | `/api/institutions/user/delete` |
| Auth(인증) | Yes (`INSTITUTION` 역할) |
| Description | 현재 기관 소속 사용자의 행을 물리 삭제하지 않고 상태를 `DELETE`로 변경한다. |

요청·응답·오류 구조는 54번과 같으며 응답 `institutionState`는 `DELETE`다.

## 4. 코드 정합성 및 확인 필요 사항

- 스캔한 Controller: 11개
- API가 있는 Controller: 11개 (`Archive`, `Care`, `Health`, `Inquiry`, `Institution`, `InstitutionMedicalTreatment`, `WardMedicalTreatment`, `TokenReissue`, `User`, `Mail`, `MyPage`)
- 빈 Controller: 없음
- 발견한 API: 56개
- 토큰 Yes: 41개
- 토큰 No: 15개
- 동일한 `HTTP Method + API Path` 중복: 없음
- `POST /api/mail/check` 실패는 HTTP 400이 아니라 HTTP 200 + Body status `"400"`이다.
- `POST /api/users/change-password`는 이름과 달리 PATCH가 아니라 실제 코드상 POST다.
- `/api/care/user/Guards`는 실제 코드에 대문자 `G`로 선언되어 있다.
- 사용자 인증·메일·토큰·이름 변경·채팅 텍스트 요청은 Jakarta Bean Validation으로 필수값과 이메일 형식을 검사한다. 회원가입 아이디는 5자 이상이어야 한다. Care·진료 요청 등 아직 검증 애노테이션을 적용하지 않은 DTO의 `필수` 표기는 서비스가 기능 수행에 요구하는 값 기준이다.
- 기관 가입·로그인 DTO와 기관 사용자 상태 변경 DTO에는 아직 Jakarta Bean Validation 애노테이션이 없으며, 상태별 기관 사용자 조회의 `JudgeUserRequest` 필드는 현재 서비스에서 사용하지 않는다.
- 일반 사용자와 기관 관리자는 JWT `role`과 Redis Refresh Token 키를 분리하며 `/api/users/token/reissue`에서 역할에 맞는 토큰으로 재발급한다.
- SMTP/Redis 장애와 별도 처리되지 않은 원시 `IOException`은 전용 전역 예외 매핑이 없어 실제 HTTP 오류 형식 확인이 필요하다. 음성 인식·파일 저장·AI 요약 예외는 HTTP 502로 매핑된다.
- `ArchiveService`의 보호자 권한 판정에 사용하는 `CareRepository.findByUserId`는 `CareState.APPROVED` 관계만 조회한다.
- 마이페이지 API는 `/api/mypage/**`에 4개가 구현되어 있으며 `/api/users/mypage`와 `/api/hearO`는 현재 코드에 존재하지 않는다.

## 5. 근거 코드

- `src/main/java/tohear/hearo/**/**Controller.java`
- `src/main/java/tohear/hearo/**/dto/request/*.java`
- `src/main/java/tohear/hearo/**/dto/response/*.java`
- `src/main/java/tohear/hearo/institution/auth/*.java`
- `src/main/java/tohear/hearo/user/auth/principal/MedicalUserArgumentResolver.java`
- `src/main/java/tohear/hearo/global/security/JwtTokenProvider.java`
- `src/main/java/tohear/hearo/global/exception/GlobalExceptionHandler.java`
- `src/main/java/tohear/hearo/global/response/Result.java`
- `src/main/java/tohear/hearo/**/**Service.java`
