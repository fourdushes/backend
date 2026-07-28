package tohear.hearo.institution.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.institution.domain.Institution;
import tohear.hearo.institution.repository.InstitutionRepository;
import tohear.hearo.user.auth.dto.response.InstitutionSearchResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public Page<InstitutionSearchResponse> search(String keyword, Pageable pageable) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        return institutionRepository.findByInstitutionNameContainingIgnoreCase(normalizedKeyword, pageable)
            .map(institution -> InstitutionSearchResponse.from(institution));
    }

    public Institution findById(Long institutionId) {
        return institutionRepository.findById(institutionId).orElseThrow(() ->
                new IllegalArgumentException("기관을 찾을 수 없습니다."));
    }

    // 기관에서 나에게 들어온 사용자(대기상태) 보여주기

    // 기관에서 나에게 들어온 사용자(승인상태) 보여주기

    // 기관에서 나에게 들어온 사용자(거절상태) 보여주기

}
