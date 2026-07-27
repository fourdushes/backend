package tohear.hearo.inquiry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tohear.hearo.global.response.Result;
import tohear.hearo.inquiry.dto.request.CreateInquiryRequest;
import tohear.hearo.inquiry.dto.response.InquiryListResponse;
import tohear.hearo.inquiry.dto.response.InquiryResponse;
import tohear.hearo.inquiry.service.InquiryService;
import tohear.hearo.user.auth.principal.CurrentMedicalUser;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result<InquiryResponse> create(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @RequestBody CreateInquiryRequest request) {
        return new Result<>("201", "문의가 등록되었습니다.", inquiryService.create(principal, request));
    }

    @GetMapping
    public Result<InquiryListResponse> findMine(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new Result<>("200", "문의 목록 조회에 성공했습니다.", inquiryService.findMine(principal, pageable));
    }

    @GetMapping("/{inquiryId}")
    public Result<InquiryResponse> findMineById(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long inquiryId) {
        return new Result<>("200", "문의 조회에 성공했습니다.",
                inquiryService.findMineById(principal, inquiryId));
    }
}
