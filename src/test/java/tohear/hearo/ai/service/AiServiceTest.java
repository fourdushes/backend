package tohear.hearo.ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import tohear.hearo.ai.dto.AiRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AiServiceTest {

    @Test
    void receivesEnglishDiseaseCodeFromAiJson() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://ai.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("http://ai.test/api/final-report"))
                .andRespond(withSuccess("""
                        {"mainSymptoms":"목 통증", "disease":"COLD"}
                        """, MediaType.APPLICATION_JSON));

        var response = new AiService(builder.build()).getSummary(new AiRequest("ward", 1L, "대화"));

        assertThat(response.getDisease()).isEqualTo("COLD");
        server.verify();
    }

    @Test
    void legacyAiResponseWithoutDiseaseStillWorks() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://ai.test");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("http://ai.test/api/final-report"))
                .andRespond(withSuccess("""
                        {"mainSymptoms":"목 통증"}
                        """, MediaType.APPLICATION_JSON));

        var response = new AiService(builder.build()).getSummary(new AiRequest("ward", 1L, "대화"));

        assertThat(response.getDisease()).isNull();
        server.verify();
    }
}
