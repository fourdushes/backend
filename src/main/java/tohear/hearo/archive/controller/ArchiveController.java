package tohear.hearo.archive.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.archive.dto.response.FindAllArchiveResponse;
import tohear.hearo.archive.dto.response.ReadArchiveResponse;
import tohear.hearo.archive.service.ArchiveService;
import tohear.hearo.global.response.Result;
import tohear.hearo.user.auth.principal.CurrentMedicalUser;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-treatment/ward/archives")
public class ArchiveController {

    private final ArchiveService archiveService;

    // 피보호자가 archiveId로 자신의 진료 기록을 조회합니다.
    @GetMapping("/{archiveId}")
    public Result getArchive(@CurrentMedicalUser MedicalUserPrincipal principal, @PathVariable Long archiveId) {
        ReadArchiveResponse response = archiveService.readArchive(principal, archiveId);
        return new Result("200", "진료 기록 조회에 성공했습니다.", response);
    }

    @GetMapping("/list")
    public Result getMethodName(@CurrentMedicalUser MedicalUserPrincipal principal,
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        FindAllArchiveResponse response = archiveService.findAllByUserId(principal, pageable);
        return new Result("200", "진료 리스트 조회에 성공했습니다", response);
    }
    
}
