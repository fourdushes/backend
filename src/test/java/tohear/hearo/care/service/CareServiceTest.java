package tohear.hearo.care.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tohear.hearo.care.domain.Care;
import tohear.hearo.care.domain.CareState;
import tohear.hearo.care.dto.request.ChangeCareStateRequest;
import tohear.hearo.care.dto.request.FindWardToCareRequest;
import tohear.hearo.care.dto.request.SaveCareRequest;
import tohear.hearo.care.repository.CareRepository;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.guardian.GuardUserRepository;
import tohear.hearo.user.ward.WardUser;
import tohear.hearo.user.ward.WardUserRepository;

@ExtendWith(MockitoExtension.class)
class CareServiceTest {

    @Mock CareRepository careRepository;
    @Mock GuardUserRepository guardRepository;
    @Mock WardUserRepository wardRepository;
    private CareService service;

    @BeforeEach
    void setUp() {
        service = new CareService(careRepository, guardRepository, wardRepository);
    }

    @Test
    void findsWardsAndMapsResponse() {
        MedicalUserPrincipal principal = new MedicalUserPrincipal("guard", UserType.GUARDIAN);
        FindWardToCareRequest request = new FindWardToCareRequest();
        request.setWardUserId("ward");
        Pageable pageable = PageRequest.of(0, 10);
        WardUser ward = new WardUser("ward-1", "홍길동", "w@test.com", "pw", UserType.WARD);
        when(careRepository.findWardUserToCare("ward", pageable))
            .thenReturn(new PageImpl<>(List.of(ward), pageable, 11));

        var response = service.findWardToCare(principal, request, pageable);

        assertThat(response.getTotalCount()).isEqualTo(11);
        assertThat(response.getCurrentPage()).isZero();
        assertThat(response.getPageSize()).isEqualTo(10);
        assertThat(response.isHasNext()).isTrue();
        assertThat(response.getWardUserList().getFirst().getWardUserId()).isEqualTo("ward-1");
        assertThat(response.getWardUserList().getFirst().getWardUserName()).isEqualTo("홍길동");
    }

    @Test
    void wardSearchFailsWhenCurrentUserIsNotGuardian() {
        MedicalUserPrincipal principal = new MedicalUserPrincipal("ward", UserType.WARD);
        FindWardToCareRequest request = new FindWardToCareRequest();
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> service.findWardToCare(principal, request, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보호자만 검색할 수 있는 기능입니다.");
    }

    @Test
    void savesPendingCareForExistingUsers() {
        MedicalUserPrincipal principal = new MedicalUserPrincipal("guard", UserType.GUARDIAN);
        SaveCareRequest request = new SaveCareRequest();
        request.setWardUserId("ward");
        GuardUser guard = guard();
        WardUser ward = ward();
        when(guardRepository.findById("guard")).thenReturn(Optional.of(guard));
        when(wardRepository.findById("ward")).thenReturn(Optional.of(ward));
        when(careRepository.save(any(Care.class))).thenAnswer(invocation -> {
            Care care = invocation.getArgument(0);
            ReflectionTestUtils.setField(care, "id", 10L);
            return care;
        });

        var response = service.saveCare(principal, request);

        assertThat(response.getCareId()).isEqualTo(10L);
        verify(careRepository).save(any(Care.class));
    }

    @Test
    void saveFailsWhenGuardianDoesNotExist() {
        MedicalUserPrincipal principal = new MedicalUserPrincipal("missing", UserType.GUARDIAN);
        SaveCareRequest request = new SaveCareRequest();

        assertThatThrownBy(() -> service.saveCare(principal, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보호자를 찾을 수 없습니다.");
    }

    @Test
    void saveFailsWhenCurrentUserIsNotGuardian() {
        MedicalUserPrincipal principal = new MedicalUserPrincipal("ward", UserType.WARD);
        SaveCareRequest request = new SaveCareRequest();
        request.setWardUserId("ward");

        assertThatThrownBy(() -> service.saveCare(principal, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보호자만 연결을 신청할 수 있습니다.");
    }

    @Test
    void approvesAndRejectsExistingCare() {
        MedicalUserPrincipal principal = new MedicalUserPrincipal("ward", UserType.WARD);
        Care care = new Care(ward(), guard());
        ReflectionTestUtils.setField(care, "id", 7L);
        ChangeCareStateRequest request = new ChangeCareStateRequest();
        request.setCareId(7L);
        when(careRepository.findByIdAndWardUser_Id(7L, "ward")).thenReturn(Optional.of(care));

        assertThat(service.approveCare(principal, request).getCareState()).isEqualTo(CareState.APPROVED);
        assertThat(service.rejectCare(principal, request).getCareState()).isEqualTo(CareState.REJECTED);
    }

    @Test
    void changingCareFailsWhenCareDoesNotBelongToCurrentWard() {
        MedicalUserPrincipal principal = new MedicalUserPrincipal("other-ward", UserType.WARD);
        ChangeCareStateRequest request = new ChangeCareStateRequest();
        request.setCareId(7L);
        when(careRepository.findByIdAndWardUser_Id(7L, "other-ward")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.approveCare(principal, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("연결 요청을 찾을 수 없거나 변경 권한이 없습니다.");
    }

    @Test
    void searchesOnlyWardsReturnedByRepository() {
        MedicalUserPrincipal principal = new MedicalUserPrincipal("guard", UserType.GUARDIAN);
        when(guardRepository.findById("guard")).thenReturn(Optional.of(guard()));
        when(careRepository.findWardUser(any(GuardUser.class))).thenReturn(List.of(ward()));

        var response = service.searchWardUsers(principal);

        assertThat(response.getTotalCount()).isOne();
        assertThat(response.getWardSearchList().getFirst().getUserType()).isEqualTo(UserType.WARD);
    }

    private GuardUser guard() {
        return new GuardUser("guard", "보호자", "g@test.com", "pw", UserType.GUARDIAN);
    }

    private WardUser ward() {
        return new WardUser("ward", "피보호자", "w@test.com", "pw", UserType.WARD);
    }
}
