package tohear.hearo.care.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import tohear.hearo.care.domain.Care;
import tohear.hearo.care.domain.CareState;
import tohear.hearo.care.dto.request.ChangeCareStateRequest;
import tohear.hearo.care.dto.request.FindWardToCareRequest;
import tohear.hearo.care.dto.request.SaveCareRequest;
import tohear.hearo.care.dto.request.WardSearchRequest;
import tohear.hearo.care.repository.CareRepository;
import tohear.hearo.user.auth.domain.UserType;
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
        FindWardToCareRequest request = new FindWardToCareRequest();
        request.setWardUserId("ward");
        WardUser ward = new WardUser("ward-1", "홍길동", "w@test.com", "pw", UserType.WARD);
        when(careRepository.findWardUserToCare("ward")).thenReturn(List.of(ward));

        var response = service.findWardToCare(request);

        assertThat(response.getTotalCount()).isOne();
        assertThat(response.getWardUserList().getFirst().getWardUserId()).isEqualTo("ward-1");
        assertThat(response.getWardUserList().getFirst().getWardUserName()).isEqualTo("홍길동");
    }

    @Test
    void savesPendingCareForExistingUsers() {
        SaveCareRequest request = new SaveCareRequest();
        request.setGuardUserId("guard");
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

        var response = service.saveCare(request);

        assertThat(response.getCareId()).isEqualTo(10L);
        verify(careRepository).save(any(Care.class));
    }

    @Test
    void saveFailsWhenGuardianDoesNotExist() {
        SaveCareRequest request = new SaveCareRequest();
        request.setGuardUserId("missing");

        assertThatThrownBy(() -> service.saveCare(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보호자를 찾을 수 없습니다.");
    }

    @Test
    void approvesAndRejectsExistingCare() {
        Care care = new Care(ward(), guard());
        ReflectionTestUtils.setField(care, "id", 7L);
        ChangeCareStateRequest request = new ChangeCareStateRequest();
        request.setCareId(7L);
        when(careRepository.findById(7L)).thenReturn(Optional.of(care));

        assertThat(service.approveCare(request).getCareState()).isEqualTo(CareState.APPROVED);
        assertThat(service.rejectCare(request).getCareState()).isEqualTo(CareState.REJECTED);
    }

    @Test
    void searchesOnlyWardsReturnedByRepository() {
        WardSearchRequest request = new WardSearchRequest();
        request.setGuardUserId("guard");
        when(guardRepository.findById("guard")).thenReturn(Optional.of(guard()));
        when(careRepository.findWardUser(any(GuardUser.class))).thenReturn(List.of(ward()));

        var response = service.searchWardUsers(request);

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
