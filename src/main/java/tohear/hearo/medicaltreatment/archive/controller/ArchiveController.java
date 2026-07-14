package tohear.hearo.medicaltreatment.archive.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.Result;
import tohear.hearo.medicaltreatment.archive.dto.response.ArchiveResponse;
import tohear.hearo.medicaltreatment.archive.service.ArchiveQueryService;
import tohear.hearo.medicaltreatment.auth.CurrentMedicalUser;
import tohear.hearo.medicaltreatment.auth.MedicalUserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-treatment/ward/archives")
public class ArchiveController {

    private final ArchiveQueryService archiveQueryService;

    // 피보호자가 진료 종료 후 archiveId로 자신의 진료 기록을 조회합니다.
    @GetMapping("/{archiveId}")
    public Result<ArchiveResponse> getArchive(
            @CurrentMedicalUser MedicalUserPrincipal principal,
            @PathVariable Long archiveId) {
        ArchiveResponse response = archiveQueryService.findArchive(archiveId, principal);
        return new Result<>("200", "진료 기록 조회에 성공했습니다.", response);
    }
}
