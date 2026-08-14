package tohear.hearo.institution.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tohear.hearo.institution.dto.response.JudgeUserDto;
import tohear.hearo.user.institution.InstitutionUserState;

public interface InstitutionRepositoryCustom {

        Page<JudgeUserDto> searchPending(Long institutionId, Pageable pageable);
        Page<JudgeUserDto> searchApproved(Long institutionId, Pageable pageable);
        Page<JudgeUserDto> searchReject(Long institutionId, Pageable pageable);
        Page<JudgeUserDto> searchInstitutionUser(Long institutionId, String keyword, InstitutionUserState institutionState, Pageable pageable);


}
