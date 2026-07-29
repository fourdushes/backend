package tohear.hearo.ai.service;

import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import tohear.hearo.ai.dto.AiRequest;
import tohear.hearo.ai.dto.AiResponse;
import tohear.hearo.global.exception.AiSummaryException;

@Service
public class AiService {

    private final RestClient restClient;

    public AiService(RestClient restClient) {
        this.restClient = restClient;
    }
    
    public AiResponse getSummary(AiRequest request) {
        try {
            AiResponse response = restClient.post()
                .uri("/api/final-report") // AI 팀이 준 엔드포인트
                .body(request)
                .retrieve()
                .body(AiResponse.class);

            validateAndNormalizeResponse(response);

            return response;
        } catch (AiSummaryException e) {
            throw e;
        } catch (RestClientException e) {
            throw new AiSummaryException("AI 요약 서비스 연결에 실패했습니다.", e);
        }
    }

    private void validateAndNormalizeResponse(AiResponse response) {
        if (response == null) {
            throw new AiSummaryException("AI 요약 응답이 없습니다.");
        }

        boolean allSummaryFieldsBlank = Stream.of(
                response.getMainSymptoms(),
                response.getDoctorOpinion(),
                response.getRemember(),
                response.getQuestionAnswer(),
                response.getDifficultWords()
            )
            .allMatch(this::isBlank);

        if (allSummaryFieldsBlank) {
            throw new AiSummaryException("AI 요약 결과가 모두 비어 있습니다.");
        }

        response.setMainSymptoms(defaultValue(response.getMainSymptoms()));
        response.setDoctorOpinion(defaultValue(response.getDoctorOpinion()));
        response.setRemember(defaultValue(response.getRemember()));
        response.setQuestionAnswer(defaultValue(response.getQuestionAnswer()));
        response.setDifficultWords(defaultValue(response.getDifficultWords()));
    }

    private String defaultValue(String value) {
        return isBlank(value) ? "없음" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
