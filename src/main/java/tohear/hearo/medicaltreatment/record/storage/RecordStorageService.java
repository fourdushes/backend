package tohear.hearo.medicaltreatment.record.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.exception.RecordStorageException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class RecordStorageService {

    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("m4a", "mp4", "webm", "wav");
    private static final Map<String, Set<String>> ALLOWED_CONTENT_TYPES = Map.of(
            "m4a", Set.of("audio/mp4", "audio/x-m4a", "application/octet-stream"),
            "mp4", Set.of("audio/mp4", "video/mp4", "application/octet-stream"),
            "webm", Set.of("audio/webm", "video/webm", "application/octet-stream"),
            "wav", Set.of("audio/wav", "audio/x-wav", "application/octet-stream"));

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, Long archiveId) {
        String extension = validateAndGetExtension(file);
        String objectKey = "records/" + archiveId + "/" + UUID.randomUUID() + "." + extension;
        String contentType = normalizeContentType(file.getContentType());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
            return objectKey;
        } catch (IOException | RuntimeException e) {
            throw new RecordStorageException("녹음 파일 저장에 실패했습니다.", e);
        }
    }

    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build());
        } catch (RuntimeException e) {
            throw new RecordStorageException("녹음 파일 정리에 실패했습니다.", e);
        }
    }

    private String validateAndGetExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("녹음 파일이 필요합니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("녹음 파일은 20MB 이하만 업로드할 수 있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank() || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("녹음 파일 확장자를 확인할 수 없습니다.");
        }

        String extension = originalFilename
                .substring(originalFilename.lastIndexOf('.') + 1)
                .toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 녹음 파일 형식입니다.");
        }

        String contentType = normalizeContentType(file.getContentType());
        if (!ALLOWED_CONTENT_TYPES.get(extension).contains(contentType)) {
            throw new IllegalArgumentException("녹음 파일의 MIME 타입이 올바르지 않습니다.");
        }
        return extension;
    }

    private String normalizeContentType(String contentType) {
        return contentType == null || contentType.isBlank()
                ? "application/octet-stream"
                : contentType.toLowerCase(Locale.ROOT);
    }
}
