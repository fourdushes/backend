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
import tohear.hearo.user.auth.domain.UserType;
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

        if (principal.getUserType() != UserType.WARD) {
            throw new IllegalArgumentException("피보호자만 본인의 진료 기록 목록을 조회할 수 있습니다.");
        }

        Page<Archive> archivePage = archiveRepository.findAllByUserId(principal.getUserId(), pageable);

        List<FindAllArchiveDto> archiveList = new ArrayList<>();

        for (Archive archive : archivePage) {
            FindAllArchiveDto findAllArchiveDto = new FindAllArchiveDto(archive.getId(), archive.getTitle(), archive.getArchiveDate());

            archiveList.add(findAllArchiveDto);
        }

        return new FindAllArchiveResponse(
                            archivePage.getTotalElements(),
                            archivePage.getNumber(),
                            archivePage.getSize(),
                            archivePage.hasNext(),
                            archiveList
                        );
    }

    // 피보호자 유저의 아카이브 목록 + 갯수 파악하기 (보호자용)
    public FindAllArchiveResponse findAllByUserId2(MedicalUserPrincipal principal, Pageable pageable, String wardUserId) {

        checkUserForList(principal, wardUserId);
        Page<Archive> archivePage = archiveRepository.findAllByUserId(wardUserId, pageable);

        List<FindAllArchiveDto> archiveList = new ArrayList<>();

        for (Archive archive : archivePage) {
            FindAllArchiveDto findAllArchiveDto = new FindAllArchiveDto(archive.getId(), archive.getTitle(), archive.getArchiveDate());

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

        checkUser(principal, findArchive);

        return new ReadArchiveResponse(
            findArchive.getId(),
            findArchive.getTitle(),
            findArchive.getArchiveDate(),
            findArchive.getText(),
            findArchive.getAllChatText()

        );
    }

    // 유저와 아카이브가 가지고 있는 유저 아이디가 맞는지 + 유저의 보호자가 맞는지 확인하는 로직 필요
    private void checkUser(MedicalUserPrincipal principal, Archive findArchive) {

        String userId = principal.getUserId();
        String wardUserId = findArchive.getWardUser().getId();

        // 피보호자는 본인 아카이브만 조회 가능
        if (principal.getUserType() == UserType.WARD) {
            if (userId.equals(wardUserId)) {
                return;
            }

            throw new IllegalArgumentException("해당 아카이브에 접근할 수 있는 권한이 없습니다.");
        }

        // 보호자는 승인된 Care 관계가 있는 경우에만 조회 가능
        if (principal.getUserType() == UserType.GUARDIAN) {
            List<Care> careList = careRepository.findByUserId(wardUserId);

            boolean isApprovedGuardian = careList.stream()
                .anyMatch(care ->
                    userId.equals(care.getGuardUser().getId())
                );

            if (isApprovedGuardian) {
                return;
            }

            throw new IllegalArgumentException("해당 아카이브에 접근할 수 있는 권한이 없습니다.");
        }

        // 기관 사용자 및 그 외 사용자 유형은 접근 불가
        throw new IllegalArgumentException("해당 아카이브에 접근할 수 있는 권한이 없습니다.");

    }

    // 유저와 아카이브가 가지고 있는 유저 아이디가 맞는지 + 유저의 보호자가 맞는지 확인하는 로직 필요
    private void checkUserForList(MedicalUserPrincipal principal, String wardUserId) {

        if (principal.getUserType() != UserType.GUARDIAN) {
            throw new IllegalArgumentException("보호자만 진료 기록 목록을 조회할 수 있습니다.");
        }

        // 보호자의 아이디를 가져와서 피보호자를 보호하는 보호자인지 확인
        List<Care> findCare = careRepository.findByUserId(wardUserId);

        boolean isApprovedGuardian = findCare.stream()
            .anyMatch(care -> principal.getUserId().equals(care.getGuardUser().getId()));

        if (!isApprovedGuardian) {
            throw new IllegalArgumentException("해당 아카이브에 접근할 수 있는 권한이 없습니다.");
        }

    }

}
