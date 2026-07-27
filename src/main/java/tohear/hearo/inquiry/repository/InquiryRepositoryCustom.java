package tohear.hearo.inquiry.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tohear.hearo.inquiry.dto.response.InquiryResponse;
import tohear.hearo.user.auth.domain.UserType;

public interface InquiryRepositoryCustom {

    Page<InquiryResponse> findMine(String userId, UserType userType, Pageable pageable);

    Optional<InquiryResponse> findMineById(Long inquiryId, String userId, UserType userType);
}
