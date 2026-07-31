package tohear.hearo.user.mypage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import tohear.hearo.institution.domain.Institution;
import tohear.hearo.institution.service.InstitutionService;
import tohear.hearo.user.auth.domain.UserType;
import tohear.hearo.user.auth.principal.MedicalUserPrincipal;
import tohear.hearo.user.guardian.GuardUser;
import tohear.hearo.user.guardian.GuardUserService;
import tohear.hearo.user.institution.InstitutionsUser;
import tohear.hearo.user.institution.InstitutionsUserService;
import tohear.hearo.user.mypage.dto.request.ChangeNameRequest;
import tohear.hearo.user.ward.WardUser;
import tohear.hearo.user.ward.WardUserService;

@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

    @Mock WardUserService wardUserService;
    @Mock GuardUserService guardUserService;
    @Mock InstitutionsUserService institutionsUserService;
    @Mock InstitutionService institutionService;

    private MyPageService service;

    @BeforeEach
    void setUp() {
        service = new MyPageService(
            wardUserService,
            guardUserService,
            institutionsUserService,
            institutionService
        );
    }

    @Test
    void wardCanReadOwnMyPage() {
        WardUser ward = new WardUser(
            "ward", "피보호자", "ward@test.com", "pw", UserType.WARD);
        when(wardUserService.findById("ward")).thenReturn(ward);

        var response = service.wardMyPage(
            new MedicalUserPrincipal("ward", UserType.WARD));

        assertThat(response.getUserId()).isEqualTo("ward");
        assertThat(response.getUsername()).isEqualTo("피보호자");
        assertThat(response.getEmail()).isEqualTo("ward@test.com");
        assertThat(response.getUserType()).isEqualTo(UserType.WARD);
    }

    @Test
    void guardianCanReadOwnMyPage() {
        GuardUser guardian = new GuardUser(
            "guard", "보호자", "guard@test.com", "pw", UserType.GUARDIAN);
        when(guardUserService.findById("guard")).thenReturn(guardian);

        var response = service.guardMyPage(
            new MedicalUserPrincipal("guard", UserType.GUARDIAN));

        assertThat(response.getUserId()).isEqualTo("guard");
        assertThat(response.getUsername()).isEqualTo("보호자");
        assertThat(response.getUserType()).isEqualTo(UserType.GUARDIAN);
    }

    @Test
    void institutionCanReadOwnMyPage() {
        Institution organization = new Institution(
                "서울병원", "admin@seoul-hospital.test", "seoul-hospital", "pw");
        ReflectionTestUtils.setField(organization, "id", 10L);
        InstitutionsUser institutionUser = new InstitutionsUser(
            "institution", "기관", "institution@test.com", "pw",
            UserType.INSTITUTIONS, organization);
        when(institutionsUserService.findById("institution")).thenReturn(institutionUser);

        var response = service.institutionsMyPage(
            new MedicalUserPrincipal("institution", UserType.INSTITUTIONS));

        assertThat(response.getUserId()).isEqualTo("institution");
        assertThat(response.getUsername()).isEqualTo("기관");
        assertThat(response.getUserType()).isEqualTo(UserType.INSTITUTIONS);
        assertThat(response.getInstitytionsName()).isEqualTo("서울병원");
    }

    @Test
    void eachUserTypeCanChangeOwnName() {
        WardUser ward = new WardUser(
            "ward", "기존 이름", "ward@test.com", "pw", UserType.WARD);
        GuardUser guardian = new GuardUser(
            "guard", "기존 이름", "guard@test.com", "pw", UserType.GUARDIAN);
        Institution organization = new Institution(
                "서울병원", "admin@seoul-hospital.test", "seoul-hospital", "pw");
        InstitutionsUser institution = new InstitutionsUser(
            "institution", "기존 이름", "institution@test.com", "pw",
            UserType.INSTITUTIONS, organization);
        when(wardUserService.findById("ward")).thenReturn(ward);
        when(guardUserService.findById("guard")).thenReturn(guardian);
        when(institutionsUserService.findById("institution")).thenReturn(institution);
        ChangeNameRequest request = new ChangeNameRequest("변경 이름");

        service.changeName(new MedicalUserPrincipal("ward", UserType.WARD), request);
        service.changeName(new MedicalUserPrincipal("guard", UserType.GUARDIAN), request);
        service.changeName(
            new MedicalUserPrincipal("institution", UserType.INSTITUTIONS), request);

        assertThat(ward.getName()).isEqualTo("변경 이름");
        assertThat(guardian.getName()).isEqualTo("변경 이름");
        assertThat(institution.getName()).isEqualTo("변경 이름");
    }

    @Test
    void userCannotReadAnotherUserTypesMyPage() {
        MedicalUserPrincipal guardian =
            new MedicalUserPrincipal("guard", UserType.GUARDIAN);

        assertThatThrownBy(() -> service.wardMyPage(guardian))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("피보호자만 볼 수 있는 기능입니다.");
    }
}
