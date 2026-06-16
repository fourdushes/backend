import { useEffect, useMemo, useRef, useState } from "react";
import {
  CheckCircle2,
  Database,
  Download,
  Mic,
  PauseCircle,
  Play,
  RefreshCcw,
  Send,
  Server,
  Smartphone,
  Trash2,
  UploadCloud,
  WifiOff
} from "lucide-react";
import { deleteRecording, getRecordings, saveRecording } from "./storage";

const uploadUrl =
  import.meta.env.VITE_RECORD_UPLOAD_URL ||
  "http://localhost:8081/api/records/upload";

const mimeCandidates = [
  "audio/webm;codecs=opus",
  "audio/webm",
  "audio/mp4",
  "audio/ogg;codecs=opus"
];

function formatDuration(seconds) {
  const mins = Math.floor(seconds / 60)
    .toString()
    .padStart(2, "0");
  const secs = Math.floor(seconds % 60)
    .toString()
    .padStart(2, "0");
  return `${mins}:${secs}`;
}

function formatBytes(bytes) {
  if (!bytes) return "0 KB";
  if (bytes < 1024 * 1024) return `${Math.round(bytes / 1024)} KB`;
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`;
}

function getSupportedMimeType() {
  return mimeCandidates.find((type) => MediaRecorder.isTypeSupported(type)) || "";
}

function createFileName(mimeType) {
  const extension = mimeType.includes("mp4")
    ? "m4a"
    : mimeType.includes("ogg")
      ? "ogg"
      : "webm";
  const timestamp = new Date().toISOString().replace(/[:.]/g, "-");
  return `hearo-recording-${timestamp}.${extension}`;
}

export default function App() {
  const [mode, setMode] = useState("web");
  const [status, setStatus] = useState("idle");
  const [seconds, setSeconds] = useState(0);
  const [error, setError] = useState("");
  const [currentRecording, setCurrentRecording] = useState(null);
  const [recordings, setRecordings] = useState([]);
  const [uploadState, setUploadState] = useState("idle");
  const [uploadMessage, setUploadMessage] = useState("");
  const [userId, setUserId] = useState("test-user");

  const mediaRecorderRef = useRef(null);
  const streamRef = useRef(null);
  const chunksRef = useRef([]);
  const timerRef = useRef(null);
  const elapsedSecondsRef = useRef(0);

  const isRecording = status === "recording";
  const isReady = status === "ready";

  const storagePath = useMemo(() => {
    if (!currentRecording) return "local://recordings/not-created";
    return `local://recordings/${currentRecording.id}`;
  }, [currentRecording]);

  useEffect(() => {
    loadRecordings();
    return () => {
      stopTracks();
      clearInterval(timerRef.current);
      if (currentRecording?.url) {
        URL.revokeObjectURL(currentRecording.url);
      }
    };
  }, []);

  async function loadRecordings() {
    const saved = await getRecordings();
    setRecordings(saved);
  }

  function stopTracks() {
    streamRef.current?.getTracks().forEach((track) => track.stop());
    streamRef.current = null;
  }

  async function startRecording() {
    setError("");
    setUploadState("idle");
    setUploadMessage("");

    if (!navigator.mediaDevices?.getUserMedia || !window.MediaRecorder) {
      setError("현재 브라우저가 녹음을 지원하지 않습니다.");
      return;
    }

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mimeType = getSupportedMimeType();
      const mediaRecorder = new MediaRecorder(
        stream,
        mimeType ? { mimeType } : undefined
      );

      chunksRef.current = [];
      streamRef.current = stream;
      mediaRecorderRef.current = mediaRecorder;

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          chunksRef.current.push(event.data);
        }
      };

      mediaRecorder.onstop = async () => {
        clearInterval(timerRef.current);
        stopTracks();

        const blob = new Blob(chunksRef.current, {
          type: mediaRecorder.mimeType || "audio/webm"
        });
        const fileName = createFileName(blob.type);
        const id = crypto.randomUUID();
        const createdAt = new Date().toISOString();
        const recording = {
          id,
          fileName,
          mimeType: blob.type,
          size: blob.size,
          duration: elapsedSecondsRef.current,
          createdAt,
          userId,
          localPath: `local://recordings/${id}`,
          blob
        };

        await saveRecording(recording);
        await loadRecordings();

        setCurrentRecording({
          ...recording,
          url: URL.createObjectURL(blob)
        });
        setStatus("ready");
      };

      elapsedSecondsRef.current = 0;
      setSeconds(0);
      timerRef.current = setInterval(() => {
        elapsedSecondsRef.current += 1;
        setSeconds(elapsedSecondsRef.current);
      }, 1000);
      mediaRecorder.start(500);
      setStatus("recording");
    } catch (err) {
      setError("마이크 권한을 허용해야 녹음을 시작할 수 있습니다.");
      stopTracks();
    }
  }

  function stopRecording() {
    if (mediaRecorderRef.current?.state === "recording") {
      mediaRecorderRef.current.stop();
      setStatus("processing");
    }
  }

  async function uploadRecording(recording = currentRecording) {
    if (!recording) return;

    setUploadState("uploading");
    setUploadMessage("");
    setError("");

    const file = new File([recording.blob], recording.fileName, {
      type: recording.mimeType
    });
    const formData = new FormData();
    formData.append("file", file);
    formData.append("userId", recording.userId || userId);
    formData.append("source", mode);
    formData.append("localPath", recording.localPath);
    formData.append("durationSeconds", String(recording.duration || seconds));
    formData.append("originalFileName", recording.fileName);

    try {
      const response = await fetch(uploadUrl, {
        method: "POST",
        body: formData
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      setUploadState("success");
      setUploadMessage("백엔드로 녹음 파일을 전달했습니다.");
    } catch (err) {
      setUploadState("offline");
      setUploadMessage(
        "백엔드 서버가 아직 없거나 응답하지 않습니다. 파일은 브라우저 로컬 저장소에 보관되어 있습니다."
      );
    }
  }

  function downloadRecording(recording = currentRecording) {
    if (!recording) return;
    const url = recording.url || URL.createObjectURL(recording.blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = recording.fileName;
    link.click();
    if (!recording.url) URL.revokeObjectURL(url);
  }

  async function removeRecording(id) {
    await deleteRecording(id);
    await loadRecordings();
    if (currentRecording?.id === id) {
      setCurrentRecording(null);
      setStatus("idle");
      setSeconds(0);
    }
  }

  return (
    <main className="app-shell">
      <section className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">hearO recorder</p>
            <h1>녹음 파일 전송 화면</h1>
          </div>
          <div className="mode-control" aria-label="화면 모드">
            <button
              className={mode === "web" ? "active" : ""}
              type="button"
              onClick={() => setMode("web")}
            >
              <Server size={16} />
              Web
            </button>
            <button
              className={mode === "app" ? "active" : ""}
              type="button"
              onClick={() => setMode("app")}
            >
              <Smartphone size={16} />
              App
            </button>
          </div>
        </header>

        <div className={`recorder-layout ${mode}`}>
          <section className="record-panel">
            <div className="record-face">
              <div className={`pulse ${isRecording ? "live" : ""}`}>
                <Mic size={44} />
              </div>
              <p className="timer">{formatDuration(seconds)}</p>
              <p className="state-text">
                {isRecording
                  ? "녹음 중"
                  : status === "processing"
                    ? "파일 생성 중"
                    : isReady
                      ? "녹음 완료"
                      : "대기 중"}
              </p>
            </div>

            <button
              className={`record-button ${isRecording ? "stop" : ""}`}
              type="button"
              onClick={isRecording ? stopRecording : startRecording}
              disabled={status === "processing"}
            >
              {isRecording ? <PauseCircle size={28} /> : <Mic size={28} />}
              {isRecording ? "녹음 중지" : "녹음 시작"}
            </button>

            {error && <p className="notice error">{error}</p>}

            {currentRecording && (
              <div className="player-strip">
                <audio src={currentRecording.url} controls />
                <div className="file-meta">
                  <strong>{currentRecording.fileName}</strong>
                  <span>
                    {formatBytes(currentRecording.size)} ·{" "}
                    {currentRecording.mimeType || "audio"}
                  </span>
                </div>
              </div>
            )}
          </section>

          <aside className="side-panel">
            <label className="field">
              <span>테스트 사용자 ID</span>
              <input
                value={userId}
                onChange={(event) => setUserId(event.target.value)}
                placeholder="user-id"
              />
            </label>

            <div className="endpoint-box">
              <div>
                <UploadCloud size={18} />
                <strong>업로드 엔드포인트</strong>
              </div>
              <code>{uploadUrl}</code>
            </div>

            <div className="path-box">
              <div>
                <Database size={18} />
                <strong>로컬 테스트 경로</strong>
              </div>
              <code>{storagePath}</code>
            </div>

            <div className="actions">
              <button
                type="button"
                onClick={() => uploadRecording()}
                disabled={!currentRecording || uploadState === "uploading"}
              >
                <Send size={18} />
                백엔드 전송
              </button>
              <button
                type="button"
                onClick={() => downloadRecording()}
                disabled={!currentRecording}
              >
                <Download size={18} />
                파일 저장
              </button>
            </div>

            {uploadState !== "idle" && (
              <p className={`notice ${uploadState}`}>
                {uploadState === "uploading" && <RefreshCcw size={16} />}
                {uploadState === "success" && <CheckCircle2 size={16} />}
                {uploadState === "offline" && <WifiOff size={16} />}
                {uploadState === "uploading"
                  ? "업로드 중입니다."
                  : uploadMessage}
              </p>
            )}
          </aside>
        </div>

        <section className="history">
          <div className="section-heading">
            <h2>로컬 녹음 목록</h2>
            <span>{recordings.length}개</span>
          </div>

          {recordings.length === 0 ? (
            <p className="empty">아직 생성된 녹음 파일이 없습니다.</p>
          ) : (
            <div className="record-list">
              {recordings.map((recording) => (
                <article className="record-item" key={recording.id}>
                  <button
                    className="play-icon"
                    type="button"
                    onClick={() => {
                      if (currentRecording?.url) {
                        URL.revokeObjectURL(currentRecording.url);
                      }
                      setCurrentRecording({
                        ...recording,
                        url: URL.createObjectURL(recording.blob)
                      });
                      setStatus("ready");
                      setSeconds(recording.duration || 0);
                    }}
                    aria-label="녹음 선택"
                  >
                    <Play size={18} />
                  </button>
                  <div>
                    <strong>{recording.fileName}</strong>
                    <span>
                      {formatBytes(recording.size)} ·{" "}
                      {new Date(recording.createdAt).toLocaleString()}
                    </span>
                  </div>
                  <button
                    className="icon-button"
                    type="button"
                    onClick={() => downloadRecording(recording)}
                    aria-label="다운로드"
                  >
                    <Download size={18} />
                  </button>
                  <button
                    className="icon-button danger"
                    type="button"
                    onClick={() => removeRecording(recording.id)}
                    aria-label="삭제"
                  >
                    <Trash2 size={18} />
                  </button>
                </article>
              ))}
            </div>
          )}
        </section>
      </section>
    </main>
  );
}
