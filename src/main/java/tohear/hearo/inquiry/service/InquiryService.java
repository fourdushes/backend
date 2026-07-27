package tohear.hearo.inquiry.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.inquiry.domain.Inquiry;
import tohear.hearo.inquiry.dto.request.CreateInquiryRequest;
import tohear.hearo.inquiry.dto.response.InquiryListResponse;
import tohear.hearo.inquiry.dto.response.InquiryResponse;
import tohear.hearo.inquiry.repository.InquiryRepository;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public InquiryResponse create(MedicalUserPrincipal principal, CreateInquiryRequest request) {
        validateRequest(request);

        Inquiry inquiry = new Inquiry(
                principal.getUserId(), principal.getUserType(),
                request.getTitle().trim(), request.getContent().trim());

        Inquiry saved = inquiryRepository.save(inquiry);
        return inquiryRepository.findMineById(
                        saved.getId(), principal.getUserId(), principal.getUserType())
                .orElseThrow(() -> new IllegalStateException("등록된 문의를 조회할 수 없습니다."));
    }

    public InquiryListResponse findMine(MedicalUserPrincipal principal, Pageable pageable) {
        Page<InquiryResponse> page = inquiryRepository.findMine(
                principal.getUserId(), principal.getUserType(), pageable);

        return new InquiryListResponse(
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getContent());
    }

    public InquiryResponse findMineById(MedicalUserPrincipal principal, Long inquiryId) {
        return inquiryRepository.findMineById(
                        inquiryId, principal.getUserId(), principal.getUserType())
                .orElseThrow(() -> new IllegalArgumentException(
                        "문의를 찾을 수 없거나 접근 권한이 없습니다."));
    }

    private void validateRequest(CreateInquiryRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("문의 제목을 입력해 주세요.");
        }
        if (request.getTitle().trim().length() > 100) {
            throw new IllegalArgumentException("문의 제목은 100자 이하여야 합니다.");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("문의 내용을 입력해 주세요.");
        }
        if (request.getContent().trim().length() > 5000) {
            throw new IllegalArgumentException("문의 내용은 5000자 이하여야 합니다.");
        }
    }

}
