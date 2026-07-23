package tohear.hearo.ai.service;

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
                .body(AiResponse.class); // 응답을 String으로 받거나 DTO로 매핑

                if (response == null || response.getSummary() == null || response.getSummary().isBlank()) {
                    throw new AiSummaryException("AI 요약 결과가 비어 있습니다.");
                }

            return response;
        } catch (AiSummaryException e) {
            throw e;
        } catch(RestClientException e) {
            throw new AiSummaryException("AI 요약 서비스 연결에 실패했습니다.", e);
        }
        
    }
}
