package tohear.hearo.medicaltreatment.medicalrequest.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.institution.InstitutionsUser;
import tohear.hearo.user.ward.WardUser;

class MedicalRequestTest {

    @Test
    void followsValidStateFlow() {
        MedicalRequest request = request();

        request.accept();
        request.start();
        request.complete();

        assertThat(request.getStatus()).isEqualTo(MedicalRequestStatus.COMPLETED);
        assertThat(request.getRespondedAt()).isNotNull();
        assertThat(request.getStartedAt()).isNotNull();
        assertThat(request.getCompletedAt()).isNotNull();
    }

    @Test
    void cannotAcceptTwiceOrStartRejectedRequest() {
        MedicalRequest accepted = request();
        accepted.accept();
        assertThatThrownBy(accepted::accept).isInstanceOf(IllegalStateException.class);

        MedicalRequest rejected = request();
        rejected.reject();
        assertThatThrownBy(rejected::start).isInstanceOf(IllegalStateException.class);
    }

    private MedicalRequest request() {
        return new MedicalRequest(
                new WardUser("ward", "환자", "ward@test.com", "pw", UserType.WARD),
                new InstitutionsUser("doctor", "의사", "doctor@test.com", "pw", UserType.INSTITUTIONS));
    }
}
