package tohear.hearo.record.clovaspeech;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class ClovaSpeechClientTest {

    @Test
    void requestEntityHasSafeDefaultsAndSerializesOptions() {
        ClovaSpeechClient.NestRequestEntity request = new ClovaSpeechClient.NestRequestEntity();
        ClovaSpeechClient.Boosting boosting = new ClovaSpeechClient.Boosting();
        boosting.setWords("보호자");
        ClovaSpeechClient.Diarization diarization = new ClovaSpeechClient.Diarization();
        diarization.setEnable(true);
        diarization.setSpeakerCountMin(1);
        diarization.setSpeakerCountMax(2);
        request.setUserdata(Map.of("recordId", 1));
        request.setBoostings(List.of(boosting));
        request.setDiarization(diarization);

        String json = new Gson().toJson(request);

        assertThat(request.getLanguage()).isEqualTo("ko-KR");
        assertThat(request.getCompletion()).isEqualTo("sync");
        assertThat(request.getWordAlignment()).isTrue();
        assertThat(request.getFullText()).isTrue();
        assertThat(json).contains("\"words\":\"보호자\"", "\"speakerCountMax\":2", "\"recordId\":1");
    }

    @Test
    void sedDefaultsToDisabled() {
        assertThat(new ClovaSpeechClient.Sed().getEnable()).isFalse();
    }
}
