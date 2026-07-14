package tohear.hearo.record.recordservice;

import java.io.File;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tohear.hearo.record.clovaspeech.ClovaSpeechClient;
import tohear.hearo.record.clovaspeech.ClovaSpeechClient.NestRequestEntity;
import tohear.hearo.record.dto.request.CompleteRecordRequest;
import tohear.hearo.record.dto.response.CompleteRecordResponse;
import tohear.hearo.record.repository.RecordRepository;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final ClovaSpeechClient clovaSpeechClient;

    public CompleteRecordResponse completeRecord(CompleteRecordRequest request) {
        NestRequestEntity requestEntity = new NestRequestEntity();
		final String result =
			clovaSpeechClient.upload(new File(request.getRecordFile()), requestEntity);
		//final String result = clovaSpeechClient.url("file URL", requestEntity); 
		//final String result = clovaSpeechClient.objectStorage("Object Storage key", requestEntity);
		System.out.println(result);
        
        return new CompleteRecordResponse(result, LocalDateTime.now().toString());
    }

}
