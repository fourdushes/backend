# hearO Recorder Frontend

React 기반 녹음 화면입니다. 브라우저 `MediaRecorder` API로 음성을 녹음하고, 녹음 파일을 `multipart/form-data`로 백엔드에 전달합니다.

## 실행

```bash
npm install
npm run dev
```

## 업로드 주소 변경

`.env` 파일을 만들고 백엔드 업로드 주소를 지정하세요.

```bash
VITE_RECORD_UPLOAD_URL=http://localhost:8081/api/records/upload
```

## 백엔드로 전달되는 FormData

- `file`: 녹음 파일
- `userId`: 테스트 사용자 ID
- `source`: `web` 또는 `app`
- `localPath`: 로컬 테스트용 경로 형태
- `durationSeconds`: 녹음 길이
- `originalFileName`: 원본 파일명

백엔드가 아직 없거나 꺼져 있으면 업로드 실패 메시지를 보여주고, 파일은 브라우저 IndexedDB에 임시 저장됩니다.
