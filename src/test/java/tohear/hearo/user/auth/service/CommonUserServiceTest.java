package tohear.hearo.user.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.guardian.GuardUserRepository;
import tohear.hearo.user.institution.InstitutionsUserRepository;
import tohear.hearo.user.ward.WardUserRepository;

@ExtendWith(MockitoExtension.class)
class CommonUserServiceTest {

    @Mock WardUserRepository wardRepository;
    @Mock GuardUserRepository guardRepository;
    @Mock InstitutionsUserRepository institutionsRepository;
    private CommonUserService service;

    @BeforeEach
    void setUp() {
        service = new CommonUserService(wardRepository, guardRepository, institutionsRepository);
    }

    @Test
    void findsUserTypeByIdInPriorityOrder() {
        when(wardRepository.existsById("ward")).thenReturn(true);
        assertThat(service.checkUserTypeById("ward")).isEqualTo(UserType.WARD);
        verifyNoInteractions(guardRepository, institutionsRepository);
    }

    @Test
    void findsGuardianAndInstitutionByEmail() {
        when(guardRepository.existsByEmail("guard@test.com")).thenReturn(true);
        assertThat(service.checkUserTypeByEmail("guard@test.com")).isEqualTo(UserType.GUARDIAN);

        when(institutionsRepository.existsByEmail("inst@test.com")).thenReturn(true);
        assertThat(service.checkUserTypeByEmail("inst@test.com")).isEqualTo(UserType.INSTITUTIONS);
    }

    @Test
    void unknownUserThrows() {
        assertThatThrownBy(() -> service.checkUserTypeById("missing"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효하지 않은 사용자 ID입니다.");
    }

    @Test
    void duplicateAcrossAnyUserTableThrows() {
        when(institutionsRepository.existsById("duplicate")).thenReturn(true);

        assertThatThrownBy(() -> service.validateDuplicateUser("duplicate"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 존재하는 회원입니다.");
    }
}
