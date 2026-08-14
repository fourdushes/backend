package tohear.hearo.institution.service;

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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tohear.hearo.global.security.JwtTokenProvider;
import tohear.hearo.institution.domain.Institution;
import tohear.hearo.institution.dto.request.SearchInstitutionUserRequest;
import tohear.hearo.institution.dto.response.JudgeUserDto;
import tohear.hearo.institution.repository.InstitutionRepository;
import tohear.hearo.user.institution.InstitutionUserState;
import tohear.hearo.user.institution.InstitutionsUserRepository;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

    @Mock InstitutionRepository institutionRepository;
    @Mock InstitutionsUserRepository institutionsUserRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider tokenProvider;
    @Mock StringRedisTemplate redisTemplate;
    private InstitutionService service;

    @BeforeEach
    void setUp() {
        service = new InstitutionService(institutionRepository, institutionsUserRepository, passwordEncoder, tokenProvider, redisTemplate);
    }

    @Test
    void searchesInstitutionUsersByAuthenticatedInstitutionKeywordAndState() {
        Institution institution = new Institution("테스트 기관", "test@naver.com", "test1", "password");
        institution.approve();
        SearchInstitutionUserRequest request = new SearchInstitutionUserRequest();
        request.setKeyword("user");
        request.setInstitutionUserState(InstitutionUserState.APPROVED);
        Pageable pageable = PageRequest.of(0, 10);
        JudgeUserDto user = new JudgeUserDto("user1", "사용자", "user@test.com", InstitutionUserState.APPROVED, "테스트 기관");
        when(institutionRepository.findById(1L)).thenReturn(Optional.of(institution));
        when(institutionRepository.searchInstitutionUser(1L, "user", InstitutionUserState.APPROVED, pageable)).thenReturn(new PageImpl<>(List.of(user), pageable, 1));

        var response = service.searchInstitutionUser(1L, request, pageable);

        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.getCurrentPage()).isZero();
        assertThat(response.getPageSize()).isEqualTo(10);
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getJudgeUserList()).containsExactly(user);
        verify(institutionRepository).searchInstitutionUser(1L, "user", InstitutionUserState.APPROVED, pageable);
    }

    @Test
    void pendingInstitutionCannotSearchInstitutionUsers() {
        Institution institution = new Institution("승인 대기 기관", "pending@naver.com", "pending", "password");
        SearchInstitutionUserRequest request = new SearchInstitutionUserRequest();
        request.setKeyword("");
        request.setInstitutionUserState(InstitutionUserState.PENDING);
        Pageable pageable = PageRequest.of(0, 10);
        when(institutionRepository.findById(2L)).thenReturn(Optional.of(institution));

        assertThatThrownBy(() -> service.searchInstitutionUser(2L, request, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("승인된 기관만 접근할 수 있습니다.");
        verify(institutionRepository, never()).searchInstitutionUser(anyLong(), anyString(), any(InstitutionUserState.class), any(Pageable.class));
    }
}
