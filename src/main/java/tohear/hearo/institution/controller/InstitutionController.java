package tohear.hearo.institution.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tohear.hearo.institution.service.InstitutionService;
import tohear.hearo.user.auth.dto.response.InstitutionSearchResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/institutions")
public class InstitutionController {

    private final InstitutionService institutionService;

    @GetMapping("/search")
    public Page<InstitutionSearchResponse> search(@RequestParam(defaultValue = "") String keyword, 
                                                  @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return institutionService.search(keyword, pageable);
    }
}