package tohear.hearo.medicaltreatment.record.recordservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import tohear.hearo.medicaltreatment.archive.service.ArchiveService;
import tohear.hearo.medicaltreatment.record.clovaspeech.ClovaSpeechClient;
import tohear.hearo.medicaltreatment.record.clovaspeech.ClovaSpeechClient.NestRequestEntity;
import tohear.hearo.medicaltreatment.record.domain.Record;
import tohear.hearo.medicaltreatment.record.dto.request.CompleteRecordRequest;
import tohear.hearo.medicaltreatment.record.dto.response.CompleteRecordResponse;
import tohear.hearo.medicaltreatment.record.repository.RecordRepository;
import tohear.hearo.user.ward.WardUserService;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final ClovaSpeechClient clovaSpeechClient;
    private final WardUserService wardUserService;
    private final ArchiveService archiveService;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    // 녹음 종료시 녹음파일을 텍스트화 하는 메서드
    public CompleteRecordResponse completeRecord(CompleteRecordRequest request) throws IOException {

        MultipartFile multipartFile = request.getFile();
        String originalFileName = multipartFile.getOriginalFilename();

        String extension = originalFileName != null && originalFileName.contains(".") ?
                                                originalFileName.substring(originalFileName.lastIndexOf(".")) : ".webm";

        Path tempPath = Files.createTempFile("hearo-record-", extension);

        try {
            multipartFile.transferTo(tempPath);
            
            String objectKey =
                "records/" + UUID.randomUUID() + extension;

            s3Client.putObject(
                PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(multipartFile.getContentType())
                .build(),
            RequestBody.fromFile(tempPath)
            );

            NestRequestEntity requestEntity = new NestRequestEntity();
            String result = clovaSpeechClient.upload(
                tempPath.toFile(),
                requestEntity
            );

            Record record = new Record(
                objectKey,
                LocalDateTime.now(),
                archiveService.findById(request.getArchiveId()),
                wardUserService.findById(request.getWardUserId())
            );

            recordRepository.save(record);


            return new CompleteRecordResponse(
                result,
                LocalDateTime.now().toString()
            );
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }

}
