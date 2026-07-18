package tohear.hearo.archive.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tohear.hearo.archive.domain.Archive;
import tohear.hearo.archive.dto.response.FindAllArchiveDto;
import tohear.hearo.archive.dto.response.FindAllArchiveResponse;
import tohear.hearo.archive.dto.response.ReadArchiveResponse;
import tohear.hearo.archive.repository.ArchiveRepository;
import tohear.hearo.care.domain.Care;
import tohear.hearo.care.repository.CareRepository;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRepository archiveRepository;
    private final CareRepository careRepository;

    public void save(Archive archive) {
        archiveRepository.save(archive);
    }

    public Archive findById(Long archiveId) {
        return archiveRepository.findById(archiveId).orElseThrow(() -> new IllegalArgumentException("저장된 파일을 찾을 수 없습니다."));
    }

    // 피보호자 유저의 아카이브 목록 + 갯수 파악하기
    public FindAllArchiveResponse findAllByUserId(MedicalUserPrincipal principal, Pageable pageable) {

        Page<Archive> archivePage = archiveRepository.findAllByUserId(principal.getUserId(), pageable);

        List<FindAllArchiveDto> archiveList = new ArrayList<>();

        for (Archive archive : archivePage) {
            FindAllArchiveDto findAllArchiveDto = new FindAllArchiveDto(archive.getId(), principal.getUserId(), archive.getArchiveDate());

            archiveList.add(findAllArchiveDto);
        }

        return new FindAllArchiveResponse(archivePage.getTotalElements(),
                              archivePage.getNumber(),
                              archivePage.getSize(),
                              archivePage.hasNext(),
                              archiveList);
    }

    // 아카이브 열기
    public ReadArchiveResponse readArchive(MedicalUserPrincipal principal, long archiveId) {
        Archive findArchive = archiveRepository.findById(archiveId).orElseThrow(() ->
            new IllegalArgumentException("진료 기록을 찾을 수 없습니다."));

        checkUser(principal.getUserId(), findArchive);

        return new ReadArchiveResponse(
            findArchive.getId(),
            findArchive.getTitle(),
            findArchive.getArchiveDate(),
            findArchive.getText(),
            findArchive.getAllChatText()

        );
    }

    // 유저와 아카이브가 가지고 있는 유저 아이디가 맞는지 + 유저의 보호자가 맞는지 확인하는 로직 필요
    private void checkUser(String userId, Archive findArchive) {

        String wardUserId = findArchive.getWardUser().getId();

        // 피보호자 본인이면 바로 허용
        if (userId.equals(wardUserId)) {
            return;
        }

        List<Care> findCare = careRepository.findByUserId(wardUserId);

        boolean isApprovedGuardian = findCare.stream().anyMatch(care -> userId.equals(care.getGuardUser().getId()));

        if (!isApprovedGuardian) {
            throw new IllegalArgumentException("해당 아카이브에 접근할 수 있는 권한이 없습니다.");
        }

    }

}
