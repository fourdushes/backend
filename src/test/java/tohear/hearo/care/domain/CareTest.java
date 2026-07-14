package tohear.hearo.care.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.ward.WardUser;

class CareTest {

    @Test
    void newCareStartsPendingAndCanBeApprovedAndRejected() {
        WardUser ward = new WardUser("ward", "피보호자", "ward@test.com", "pw", UserType.WARD);
        GuardUser guard = new GuardUser("guard", "보호자", "guard@test.com", "pw", UserType.GUARDIAN);

        Care care = new Care(ward, guard);

        assertThat(care.getCareState()).isEqualTo(CareState.PENDING);
        assertThat(care.getCreatedAt()).isNotNull();
        assertThat(care.getUpdatedAt()).isNotNull();
        assertThat(care.getWardUser()).isSameAs(ward);
        assertThat(care.getGuardUser()).isSameAs(guard);

        care.approve();
        assertThat(care.getCareState()).isEqualTo(CareState.APPROVED);

        care.reject();
        assertThat(care.getCareState()).isEqualTo(CareState.REJECTED);
    }

    @Test
    void careStateDescriptionsAreExposed() {
        assertThat(CareState.APPROVED.getDescription()).isEqualTo("승인됨");
        assertThat(CareState.PENDING.getDescription()).isEqualTo("승인 대기 중");
        assertThat(CareState.REJECTED.getDescription()).isEqualTo("거절됨");
    }
}
