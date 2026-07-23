package tohear.hearo.medicaltreatment.record.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordStorageServiceTest {

    @Mock
    private S3Client s3Client;

    private RecordStorageService service;

    @BeforeEach
    void setUp() {
        service = new RecordStorageService(s3Client);
        ReflectionTestUtils.setField(service, "bucket", "test-bucket");
    }

    @Test
    void uploadsRecordingWithGeneratedArchiveKey() {
        MockMultipartFile file =
                new MockMultipartFile("file", "voice.m4a", "audio/mp4", new byte[] {1, 2, 3});
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

        String objectKey = service.upload(file, 10L);

        assertThat(objectKey).startsWith("records/10/").endsWith(".m4a");
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        assertThat(requestCaptor.getValue().bucket()).isEqualTo("test-bucket");
        assertThat(requestCaptor.getValue().key()).isEqualTo(objectKey);
        assertThat(requestCaptor.getValue().contentType()).isEqualTo("audio/mp4");
    }

    @Test
    void rejectsUnsupportedExtensionAndOversizedFile() {
        MockMultipartFile unsupported =
                new MockMultipartFile("file", "voice.txt", "text/plain", new byte[] {1});
        MockMultipartFile oversized =
                new MockMultipartFile("file", "voice.webm", "audio/webm", new byte[20 * 1024 * 1024 + 1]);

        assertThatThrownBy(() -> service.upload(unsupported, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 녹음 파일 형식입니다.");
        assertThatThrownBy(() -> service.upload(oversized, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("녹음 파일은 20MB 이하만 업로드할 수 있습니다.");
    }

    @Test
    void deletesUploadedObjectByKey() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(null);

        service.delete("records/10/voice.m4a");

        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(requestCaptor.capture());
        assertThat(requestCaptor.getValue().bucket()).isEqualTo("test-bucket");
        assertThat(requestCaptor.getValue().key()).isEqualTo("records/10/voice.m4a");
    }
}
