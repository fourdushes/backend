package tohear.hearo.institution.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tohear.hearo.global.security.JwtTokenProvider;
import tohear.hearo.institution.domain.Institution;
import tohear.hearo.institution.dto.request.ChangeStateRequest;
import tohear.hearo.institution.dto.request.IdFindRequest;
import tohear.hearo.institution.dto.request.InstitutionChangePasswordRequest;
import tohear.hearo.institution.dto.request.InstitutionJoinRequest;
import tohear.hearo.institution.dto.request.InstitutionLoginRequest;
import tohear.hearo.institution.dto.request.InstitutionToChangePasswordRequest;
import tohear.hearo.institution.dto.request.JudgeUserRequest;
import tohear.hearo.institution.dto.response.ChangeStateResponse;
import tohear.hearo.institution.dto.response.InstitutionJoinResponse;
import tohear.hearo.institution.dto.response.InstitutionLoginResponse;
import tohear.hearo.institution.dto.response.InstitutionToChangePasswordResponse;
import tohear.hearo.institution.dto.response.JudgeUserDto;
import tohear.hearo.institution.dto.response.JudgeUserResponse;
import tohear.hearo.institution.repository.InstitutionRepository;
import tohear.hearo.user.auth.dto.response.InstitutionSearchResponse;
import tohear.hearo.user.institution.InstitutionState;
import tohear.hearo.user.institution.InstitutionsUser;
import tohear.hearo.user.institution.InstitutionsUserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionsUserRepository institutionsUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final StringRedisTemplate redisTemplate;

    public Page<InstitutionSearchResponse> search(String keyword, Pageable pageable) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        return institutionRepository.findByInstitutionNameContainingIgnoreCase(normalizedKeyword, pageable)
            .map(institution -> InstitutionSearchResponse.from(institution));
    }

    public Institution findById(Long institutionId) {
        return institutionRepository.findById(institutionId).orElseThrow(() ->
                new IllegalArgumentException("기관을 찾을 수 없습니다."));
    }

    // 회원가입
    @Transactional
    public InstitutionJoinResponse join(InstitutionJoinRequest request) {
        validateDuplicateId(request.getInstitutionId());
        checkPassword(request.getPassword(), request.getCheckPassword());

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Institution institution = new Institution(
            request.getInstitutionName(),
            request.getEmail(),
            request.getInstitutionId(),
            encodedPassword
        );

        institutionRepository.save(institution);

        return new InstitutionJoinResponse(institution.getId());
    }

    // 기관 테이블에서 회원가입시 이메일 중복이 있는지 체크
    public boolean existsByEmail(String mail) {
        String standardEmail = mail.trim().toLowerCase();

        return institutionRepository.existsByEmail(standardEmail);

    }

    // 중복이 있다면 오류 발생
    public void validateEmailAvailable(String email) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
    }

    public void validateDuplicateId(String id) { // 아이디 중복 검증
        if (institutionRepository.existsByInstitutionLoginId(id)) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
    }

    public void checkPassword(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 로그인
    @Transactional
    public InstitutionLoginResponse validateLogin(InstitutionLoginRequest request) { // 로그인 검증
        Institution institution = institutionRepository.findByInstitutionLoginId(request.getLoginId()).orElseThrow(
            () -> new IllegalArgumentException("아이디가 올바르지 않습니다. "));

        if (!passwordEncoder.matches(request.getPassword(), institution.getPassward())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        if (institution.getInstitutionState() != tohear.hearo.institution.domain.InstitutionState.APPROVED) {
            throw new IllegalArgumentException("승인된 기관만 로그인할 수 있습니다.");
        }

        String accessToken = tokenProvider.createAccessToken(institution.getId());
        String refreshToken = tokenProvider.createRefreshToken(institution.getId());

        redisTemplate.opsForValue().set(
            "refresh-token:institution:" + institution.getId(),
            refreshToken,
            Duration.ofMillis(tokenProvider.getRefreshTokenValidityInMilliseconds())
        );

        return new InstitutionLoginResponse(accessToken, institution.getId(), refreshToken);
    }

    // 아이디 찾기
    public String findId(IdFindRequest request) {
        return institutionRepository
            .findByInstitutionNameAndEmailIgnoreCase(
                request.getInstitutionName(),
                request.getEmail().trim().toLowerCase()
            )
            .map(Institution::getInstitutionLoginId)
            .orElseThrow(() -> new IllegalArgumentException("아이디를 찾을 수 없습니다."));
    }

    public InstitutionToChangePasswordResponse validateToChangePassword(InstitutionToChangePasswordRequest request) {

        String email = request.getEmail().trim().toLowerCase();
        String verified = redisTemplate.opsForValue().get("mail-verified:" + email);

        if (!"true".equals(verified)) {
            throw new IllegalArgumentException(
                "이메일 인증이 완료되지 않았습니다. 이메일 인증을 먼저 진행해주세요."
            );
        }

        Institution institution = institutionRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("이메일이 올바르지 않습니다. " + request.getEmail()));

        if (!institution.getInstitutionName().equals(request.getInstitutionName())) {
            throw new IllegalArgumentException(
                "기관명이 올바르지 않습니다. " + request.getInstitutionName()
            );
        }

        String token = UUID.randomUUID().toString();
        String redisKey = "institution-password-reset:" + institution.getId();

        redisTemplate.opsForValue().set(redisKey, token, Duration.ofMinutes(3));

        return new InstitutionToChangePasswordResponse(institution.getId(), token);
    }

    @Transactional
    public Long changePassword(InstitutionChangePasswordRequest request) {
        String redisKey = "institution-password-reset:" + request.getInstitutionId();
        String savedToken = redisTemplate.opsForValue().get(redisKey);

        if (savedToken == null || !savedToken.equals(request.getTempToken())) {
            throw new IllegalArgumentException(
                "인증 시간이 만료되었거나 유효하지 않은 접근입니다. 처음부터 다시 시도해주세요."
            );
        }

        if (!request.getNewPassword().equals(request.getCheckNewPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        int updatedCount = institutionRepository.updatePassword(
            request.getInstitutionId(),
            encodedNewPassword
        );

        if (updatedCount == 0) {
            throw new IllegalArgumentException("기관을 찾을 수 없습니다.");
        }

        redisTemplate.delete(redisKey);

        return request.getInstitutionId();
    }

    // 기관에서 나에게 들어온 사용자(대기상태) 보여주기
    public JudgeUserResponse searchPending(Long institutionId, JudgeUserRequest request, Pageable pageable) {
        requireApprovedInstitution(institutionId);
        Page<JudgeUserDto> page = institutionRepository.searchPending(institutionId, pageable);

        return new JudgeUserResponse(
            page.getTotalElements(),
            page.getNumber(),
            page.getSize(),
            page.hasNext(),
            page.getContent()
        );
    }

    // 기관에서 나에게 들어온 사용자(승인상태) 보여주기
    public JudgeUserResponse searchApproved(Long institutionId, JudgeUserRequest request, Pageable pageable) {
        requireApprovedInstitution(institutionId);
        Page<JudgeUserDto> page = institutionRepository.searchApproved(institutionId, pageable);

        return new JudgeUserResponse(
            page.getTotalElements(),
            page.getNumber(),
            page.getSize(),
            page.hasNext(),
            page.getContent()
        );
    }

    // 기관에서 나에게 들어온 사용자(거절상태) 보여주기
    public JudgeUserResponse searchReject(Long institutionId, JudgeUserRequest request, Pageable pageable) {
        requireApprovedInstitution(institutionId);
        Page<JudgeUserDto> page = institutionRepository.searchReject(institutionId, pageable);

        return new JudgeUserResponse(
            page.getTotalElements(),
            page.getNumber(),
            page.getSize(),
            page.hasNext(),
            page.getContent()
        );
    }

    // 연경 승인
    @Transactional
    public ChangeStateResponse approvedState(Long institutionId, ChangeStateRequest request) {
        requireApprovedInstitution(institutionId);
        InstitutionsUser institutionsUser = institutionsUserRepository.findById(request.getInstitutionsUserId()).orElseThrow(
            () -> new IllegalArgumentException("기관 사용자를 찾을 수 없습니다."));

        validateInstitutionUserOwner(institutionId, institutionsUser);
        institutionsUser.approved();

        return new ChangeStateResponse(institutionsUser.getId(), InstitutionState.APPROVED);
    }

    // 연결 거절
    @Transactional
    public ChangeStateResponse rejectState(Long institutionId, ChangeStateRequest request) {
        requireApprovedInstitution(institutionId);
        InstitutionsUser institutionsUser = institutionsUserRepository.findById(request.getInstitutionsUserId()).orElseThrow(
            () -> new IllegalArgumentException("기관 사용자를 찾을 수 없습니다."));

        validateInstitutionUserOwner(institutionId, institutionsUser);
        institutionsUser.reject();

        return new ChangeStateResponse(institutionsUser.getId(), InstitutionState.REJECTED);
    }

    // 승인된 사용자 삭제
    @Transactional
    public ChangeStateResponse deleteState(Long institutionId, ChangeStateRequest request) {
        requireApprovedInstitution(institutionId);
        InstitutionsUser institutionsUser = institutionsUserRepository.findById(request.getInstitutionsUserId()).orElseThrow(
            () -> new IllegalArgumentException("기관 사용자를 찾을 수 없습니다."));

        validateInstitutionUserOwner(institutionId, institutionsUser);
        institutionsUser.delete();

        return new ChangeStateResponse(institutionsUser.getId(), InstitutionState.DELETE);
    }

    private void requireApprovedInstitution(Long institutionId) {
        Institution institution = findById(institutionId);
        if (institution.getInstitutionState() != tohear.hearo.institution.domain.InstitutionState.APPROVED) {
            throw new IllegalArgumentException("승인된 기관만 접근할 수 있습니다.");
        }
    }

    private void validateInstitutionUserOwner(Long institutionId, InstitutionsUser institutionsUser) {
        if (!institutionsUser.getInstitution().getId().equals(institutionId)) {
            throw new IllegalArgumentException("해당 기관의 사용자가 아닙니다.");
        }
    }
}
