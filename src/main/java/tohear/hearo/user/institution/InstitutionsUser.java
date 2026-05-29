package tohear.hearo.user.institution;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import lombok.Getter;
import tohear.hearo.user.auth.domain.UserType;

@Entity
@Getter
public class InstitutionsUser {

    @Id
    @Column(name = "institutions_user_id")
    private String id;
    private String name; // 기관 사용자 이름
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType userType; // 사용자 유형 (기관 사용자)

    public InstitutionsUser() {
    }

    public InstitutionsUser(String id, String name, String email, String password, UserType userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}
